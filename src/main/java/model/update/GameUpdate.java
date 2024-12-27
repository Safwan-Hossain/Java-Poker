package model.update;

import enumeration.UpdateType;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class GameUpdate implements Serializable {
    private final UpdateType updateType;
    private final String updateId;
    private final LocalDateTime creationTime;
    private LocalDateTime broadcastTime;
    private LocalDateTime receivedTime;

    protected GameUpdate(UpdateType updateType) {
        this.updateType = updateType;
        this.updateId = UUID.randomUUID().toString();
        this.creationTime = LocalDateTime.now();
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public String getUpdateId() {
        return updateId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getBroadcastTime() {
        return broadcastTime;
    }

    public void setBroadcastTime(LocalDateTime broadcastTime) {
        this.broadcastTime = broadcastTime;
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Duration getCreationToBroadcastDelay() {
        if (creationTime != null && broadcastTime != null) {
            return Duration.between(creationTime, broadcastTime);
        }
        return null;
    }

    public Duration getBroadcastToReceivedDelay() {
        if (broadcastTime != null && receivedTime != null) {
            return Duration.between(broadcastTime, receivedTime);
        }
        return null;
    }

    public String getTimingsInfo(boolean singleLine) {
        String separator = singleLine ? " | " : "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("Update ID: ").append(updateId).append(separator);
        sb.append("Update Type: ").append(updateType).append(separator);
        sb.append("Creation Time: ").append(creationTime).append(separator);
        sb.append("Broadcast Time: ").append(broadcastTime != null ? broadcastTime : "Not Broadcasted Yet").append(separator);
        sb.append("Received Time: ").append(receivedTime != null ? receivedTime : "Not Received Yet").append(separator);
        return sb.toString();
    }

    public String getDelaysInfo(boolean singleLine) {
        String separator = singleLine ? " | " : "\n";
        StringBuilder sb = new StringBuilder();
        Duration creationToBroadcastDelay = getCreationToBroadcastDelay();
        Duration broadcastToReceivedDelay = getBroadcastToReceivedDelay();

        sb.append("Creation to Broadcast Delay: ")
                .append(creationToBroadcastDelay != null ? creationToBroadcastDelay.toMillis() + " ms" : "N/A")
                .append(separator);

        sb.append("Broadcast to Received Delay: ")
                .append(broadcastToReceivedDelay != null ? broadcastToReceivedDelay.toMillis() + " ms" : "N/A")
                .append(separator);

        return sb.toString();
    }

    public String getTimingsAndDelaysInfo(boolean singleLine) {
        return getTimingsInfo(singleLine) + getDelaysInfo(singleLine);
    }

    @Override
    public String toString() {
        return "GameUpdate{" +
                "updateType=" + updateType +
                ", updateId='" + updateId + '\'' +
                ", creationTime=" + creationTime +
                ", broadcastTime=" + broadcastTime +
                ", receivedTime=" + receivedTime +
                '}';
    }
}
