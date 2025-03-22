package com.poker.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.infrastructure.websocket.GameWebSocketHandler;
import com.poker.services.GameTableRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Map;

@Configuration
@EnableWebFlux
public class WebSocketConfig {

    @Bean
    public GameWebSocketHandler gameWebSocketHandler(GameTableRegistry tableRegistry, ObjectMapper objectMapper) {
        return new GameWebSocketHandler(tableRegistry, objectMapper);
    }


    // maps websocket requests to "/ws/game/{tableId}" and routes
    // them to GameWebSocketHandler, which then calls the handle() method
    @Bean
    public SimpleUrlHandlerMapping handlerMapping(GameWebSocketHandler gameWebSocketHandler) {
        Map<String, Object> urlMap = Map.of(
            "/ws/game/{tableId}", gameWebSocketHandler, // Existing games
                "/ws/game", gameWebSocketHandler           // New games with query params
    );
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(urlMap);
        mapping.setOrder(-1); // makessure it takes precedence over other mappings
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
