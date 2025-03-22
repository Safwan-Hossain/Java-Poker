package com.poker.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TimeoutService {

    private static final Duration SESSION_TIMEOUT_DURATION = Duration.ofMinutes(10);
    private static final Duration INACTIVITY_TIMEOUT_DURATION = Duration.ofMinutes(3);
    private static final Duration PLAYER_MOVE_TIMEOUT_DURATION = Duration.ofSeconds(30);

    private final Map<String, Disposable> sessionTimeouts = new ConcurrentHashMap<>();
    private final Map<String, TimeoutTask> inactivityTimeouts = new ConcurrentHashMap<>();
    private final Map<String, TimeoutTask> playerMoveTimeouts = new ConcurrentHashMap<>();

    public void scheduleSessionTimeout(String sessionId, Runnable onTimeout) {
        reschedule(sessionTimeouts, sessionId, SESSION_TIMEOUT_DURATION, onTimeout);
    }

    public void scheduleInactivityTimeout(String sessionId, Runnable onTimeout) {
        rescheduleInactivity(inactivityTimeouts, sessionId, INACTIVITY_TIMEOUT_DURATION, onTimeout);
    }

    public void resetInactivityTimeout(String sessionId) {
        TimeoutTask task = inactivityTimeouts.get(sessionId);
        if (task != null) {
            task.disposable.dispose();
            rescheduleInactivity(inactivityTimeouts, sessionId, INACTIVITY_TIMEOUT_DURATION, task.onTimeout);
        }
    }

    public void startPlayerMoveTimeout(String sessionId, Runnable onTimeout) {
        cancelPlayerMoveTimeout(sessionId);
        Disposable disposable = Mono.delay(PLAYER_MOVE_TIMEOUT_DURATION)
                .doOnError(e -> handleTimeoutError(sessionId, e))
                .doOnNext(tick -> {
                    cancelPlayerMoveTimeout(sessionId);
                    onTimeout.run();
                })
                .subscribe();

        playerMoveTimeouts.put(sessionId, new TimeoutTask(disposable, onTimeout));
    }

    public void cancelPlayerMoveTimeout(String sessionId) {
        TimeoutTask task = playerMoveTimeouts.remove(sessionId);
        if (task != null && !task.disposable.isDisposed()) {
            task.disposable.dispose();
        }
    }

    public void cancelSessionTimeout(String sessionId) {
        cancel(sessionTimeouts, sessionId);
    }

    public void cancelInactivityTimeout(String sessionId) {
        TimeoutTask task = inactivityTimeouts.remove(sessionId);
        if (task != null && !task.disposable.isDisposed()) {
            task.disposable.dispose();
        }
    }

    public void cancelAll(String sessionId) {
        cancelSessionTimeout(sessionId);
        cancelInactivityTimeout(sessionId);
        cancelPlayerMoveTimeout(sessionId);
    }

    private void reschedule(Map<String, Disposable> map, String sessionId, Duration duration, Runnable onTimeout) {
        cancel(map, sessionId);
        Disposable disposable = Mono.delay(duration)
                .doOnError(e -> handleTimeoutError(sessionId, e))
                .doOnNext(tick -> {
                    cancel(map, sessionId);
                    onTimeout.run();
                })
                .subscribe();

        Disposable previous = map.put(sessionId, disposable);
        if (previous != null && !previous.isDisposed()) {
            previous.dispose();
        }
    }

    private void rescheduleInactivity(Map<String, TimeoutTask> map, String sessionId, Duration duration, Runnable onTimeout) {
        cancelInactivityTimeout(sessionId);
        Disposable disposable = Mono.delay(duration)
                .doOnError(e -> handleTimeoutError(sessionId, e))
                .doOnNext(tick -> {
                    cancelInactivityTimeout(sessionId);
                    onTimeout.run();
                })
                .subscribe();

        map.put(sessionId, new TimeoutTask(disposable, onTimeout));
    }

    private void cancel(Map<String, Disposable> map, String sessionId) {
        Disposable timeout = map.remove(sessionId);
        if (timeout != null && !timeout.isDisposed()) {
            timeout.dispose();
        }
    }

    private void handleTimeoutError(String sessionId, Throwable error) {
        log.error("TIME OUT ERROR: Failed to schedule timer for session ID: [{}]", sessionId, error);
    }

    private record TimeoutTask(Disposable disposable, Runnable onTimeout) {}
}
