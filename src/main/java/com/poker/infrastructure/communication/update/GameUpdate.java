package com.poker.infrastructure.communication.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.poker.enumeration.GameUpdateType;
import com.poker.infrastructure.communication.update.impl.*;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.poker.constants.Constants.TIME_FORMAT;
import static com.poker.constants.Constants.TIME_ZONE;
import static com.poker.constants.GameUpdateTypeDescriptions.*;

@Data
@SuperBuilder
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = UPDATE_TYPE_FIELD  // this determines which subclass to use
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayerActionUpdate.class, name = PLAYER_ACTION_UPDATE_DESC),
        @JsonSubTypes.Type(value = PlayerSetupUpdate.class, name = PLAYER_SETUP_UPDATE_DESC),
        @JsonSubTypes.Type(value = PlayerTurnUpdate.class, name = PLAYER_TURN_UPDATE_DESC),
        @JsonSubTypes.Type(value = RoundStateUpdate.class, name = ROUND_STATE_UPDATE_DESC),
        @JsonSubTypes.Type(value = ServerMessageUpdate.class, name = SERVER_MESSAGE_UPDATE_DESC),
        @JsonSubTypes.Type(value = ConnectionStatusUpdate.class, name = CONNECTION_STATUS_UPDATE_DESC),
        @JsonSubTypes.Type(value = ShowdownResultUpdate.class, name = SHOWDOWN_RESULT_UPDATE_DESC),
        @JsonSubTypes.Type(value = GameStateSnapshotUpdate.class, name = GAME_STATE_SNAPSHOT_UPDATE_DESC)
})

public abstract class GameUpdate implements Serializable {
    private final String updateId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private final Instant creationTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private Instant broadcastTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT, timezone = TIME_ZONE)
    private Instant receivedTime;

    protected GameUpdate() {
        this.updateId = UUID.randomUUID().toString();
        this.creationTime = Instant.now();
    }

    @JsonProperty(UPDATE_TYPE_FIELD)
    public abstract GameUpdateType getUpdateType();

    public void recordBroadcastTime() {
        this.broadcastTime = Instant.now();
    }

    public Optional<Duration> getCreationToBroadcastDelay() {
        return calculateDuration(creationTime, broadcastTime);
    }

    public Optional<Duration> getBroadcastToReceivedDelay() {
        return calculateDuration(broadcastTime, receivedTime);
    }

    private Optional<Duration> calculateDuration(Instant start, Instant end) {
        return (start != null && end != null) ? Optional.of(Duration.between(start, end)) : Optional.empty();
    }

    public String getTimingsInfo(boolean singleLine) {
        String separator = singleLine ? " | " : "\n";
        return String.join(separator,
                "Update ID: " + updateId,
                "Creation Time: " + creationTime,
                "Broadcast Time: " + formatTime(broadcastTime, "Not Broadcasted Yet"),
                "Received Time: " + formatTime(receivedTime, "Not Received Yet")
        );
    }

    public String getDelaysInfo(boolean singleLine) {
        String separator = singleLine ? " | " : "\n";
        return String.join(separator,
                "Creation to Broadcast Delay: " + getDelayString(getCreationToBroadcastDelay()),
                "Broadcast to Received Delay: " + getDelayString(getBroadcastToReceivedDelay())
        );
    }

    public String getTimingsAndDelaysInfo(boolean singleLine) {
        String separator = singleLine ? " | " : "\n";
        return getTimingsInfo(singleLine) + separator + getDelaysInfo(singleLine);
    }

    private String formatTime(Instant time, String defaultValue) {
        return (time != null) ? time.toString() : defaultValue;
    }

    private String getDelayString(Optional<Duration> delay) {
        return delay.map(d -> d.toMillis() + " ms").orElse("N/A");
    }

}
