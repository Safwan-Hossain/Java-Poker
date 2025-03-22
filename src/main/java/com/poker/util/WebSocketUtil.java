package com.poker.util;

import com.poker.constants.Constants;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import static com.poker.constants.Constants.*;

public class WebSocketUtil {

    // only allows letters, numbers, underscores, dashes, and spaces
    private static final Pattern VALID_PLAYER_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-\\s]+$");

    private static final int MAX_PLAYER_NAME_CHARACTERS = 50;

    public static Map<String, String> extractQueryParams(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap();
    }

    public static String extractTableId(WebSocketSession session) {
        if (session == null || session.getHandshakeInfo().getUri().getPath() == null) {
            return "";
        }

        String path = session.getHandshakeInfo().getUri().getPath();
        String[] segments = Arrays.stream(path.split("/"))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);

        return segments.length > 0 ? segments[segments.length - 1] : "";
    }

    public static String extractPlayerName(Map<String, String> queryParams) {
        String playerName = queryParams.getOrDefault(Constants.PLAYER_NAME_PARAM, DEFAULT_PLAYER_NAME_PARAM).trim();
        playerName = decode(playerName);

        if (playerName.isEmpty() || playerName.length() > MAX_PLAYER_NAME_CHARACTERS) {
            throw new IllegalArgumentException("Invalid player name: " + playerName);
        }

        // Validate that the player name only contains allowed characters.
        if (!VALID_PLAYER_NAME_PATTERN.matcher(playerName).matches()) {
            throw new IllegalArgumentException("Player name contains invalid characters: " + playerName);
        }

        return playerName;
    }


    public static boolean isNewGame(Map<String, String> queryParams) {
        String newGameValue = queryParams.getOrDefault(NEW_GAME_PARAM, FALSE_STRING_VALUE);
        return Boolean.parseBoolean(newGameValue);
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode player name", e);
        }
    }
}
