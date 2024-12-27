package controller;

import model.client.Client;
import model.game.Game;
import model.update.GameUpdate;
import model.update.GameUpdateHandler;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.CheckedSupplier;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientController {

    private boolean ignoreUnexpectedClass = true;
    private static final int NUM_OF_RETRIES = 10;
    private static final int DELAY_BETWEEN_RETRIES_SECONDS = 1;
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    private final Client client;
    private final RetryPolicy<Object> retryPolicy;
    private final ScheduledExecutorService executorService;

    private final GameUpdateHandler updateHandler;

    public ClientController(String hostIp, int port, String username, Game game) throws IOException {
        this.client = new Client(hostIp, port, username);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.retryPolicy = setupRetryPolicy();
        this.updateHandler = new GameUpdateHandler(game);
    }

    public void setIgnoreUnexpectedClass(boolean shouldIgnore) {
        this.ignoreUnexpectedClass = shouldIgnore;
    }

    public void handleClassNotFoundError(ClassNotFoundException e) throws ClassNotFoundException {
        if (ignoreUnexpectedClass) {
            throw e;
        }
        else {
            logger.error("Received data from server of unexpected type: ", e);
        }
    }

    private RetryPolicy<Object> setupRetryPolicy() {
        return new RetryPolicy<>()
                .handle(IOException.class)
                .withDelay(Duration.ofSeconds(DELAY_BETWEEN_RETRIES_SECONDS))
                .withMaxRetries(NUM_OF_RETRIES)
                .onFailedAttempt(event ->
                        logger.warn("Attempt {}/{} failed. Reason: {}",
                                event.getAttemptCount(),
                                NUM_OF_RETRIES,
                                event.getLastFailure().toString())
                )
                .onRetry(event ->
                        logger.info("Retry {}/{}. Retrying in {} seconds...",
                                event.getAttemptCount(),
                                NUM_OF_RETRIES,
                                DELAY_BETWEEN_RETRIES_SECONDS)
                )
                .onRetriesExceeded(event ->
                        logger.error("Retries exceeded after {} attempts. Last failure: {}",
                                event.getAttemptCount(),
                                event.getFailure().toString())
                );
    }

    public long getLastUpdateNumber(){
        return 1L;
    }
    public GameUpdate getLastUpdate() {
        return null;
    }
    public boolean isGameSynchronized() {
        return true;
    }

    // TODO - how should we handle receive errors? If error occurred during receive message,
    //  attempt to ask server for last update number. If send data to server fails, it will reconnect automatically.
    //  if update number matches to our update number, ignore and carry on. If update number does not match, request most recent update
    public GameUpdate receiveMessage() throws IOException, ClassNotFoundException {
        GameUpdate update = receiveMessageInternal();
        if (update != null) {
            return update;
        }
        return update;
    }

    public GameUpdate receiveMessageInternal() throws IOException, ClassNotFoundException {
        GameUpdate update = null;
        while (update == null) {
            try {
                update = client.receiveMessage();
            } catch (ClassNotFoundException e) {
                handleClassNotFoundError(e);
            }
        }
        updateHandler.handleUpdate(update);
        return update;
    }

    // Try to send message with multiple retries. If failed to send data, disconnect and reconnect to the server, then
    // try to send message again;
    public boolean sendMessage(GameUpdate message) throws IOException {
        boolean messageSentSuccessfully = sendMessageWithRetries(message);
        if (messageSentSuccessfully) {
            return true;
        }

        boolean reconnectSuccessful = attemptReconnect();
        if (!reconnectSuccessful) {
            throw new IOException("Failed to reconnect to server");
        }

        messageSentSuccessfully = sendMessageInternal(message);

        if (messageSentSuccessfully) {
            return true;
        }
        else {
            throw new IOException("Failed to send data to server after reconnection");
        }
    }

    private boolean sendMessageInternal(GameUpdate message) {
        try {
            client.sendMessage(message);
            return true;
        } catch (IOException e) {
            logger.error("Failed to send message", e);
            return false;
        }
    }


    private boolean sendMessageWithRetries(GameUpdate message) throws IOException {
        CheckedRunnable sendMessageOperation = () -> client.sendMessage(message);
        try {
            Failsafe.with(retryPolicy)
                    .with(executorService)
                    .runAsync(sendMessageOperation)
                    .get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send message after retries", e);
            return false;
        }
    }

    public boolean attemptReconnect() {
        CheckedSupplier<Boolean> reconnectOperation = () -> {
            client.reconnect();
            return true;
        };
        try {
            return Failsafe.with(retryPolicy)
                    .with(executorService)
                    .getAsync(reconnectOperation)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to reconnect to server after retries", e);
            return false;
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
