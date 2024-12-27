package model;

import model.update.GameUpdate;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String CLOSED_EVERYTHING_MSG = "CLIENT: CLOSED EVERYTHING";
    private static final String ERROR_CLOSING_RESOURCES_MSG = "Error while closing resources: ";

    private String hostIp;
    private int port;
    private String clientID;
    private String clientName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(String hostIp, int port, String clientName) throws IOException {
        if (StringUtils.isBlank(hostIp) || StringUtils.isBlank(clientName)) {
            throw new IllegalArgumentException("Cannot create new client. Invalid Host IP or client name");
        }

        this.hostIp = hostIp;
        this.port = port;
        this.clientName = clientName;
        this.clientID = generateClientID();
        tryConnectingToServer();
        initializeStreams();
    }
    public Client(Socket socket, String clientName) throws IOException {
        if (socket == null || clientName == null || clientName.isEmpty()) {
            throw new IllegalArgumentException("Socket and client name cannot be null or empty");
        }

        this.socket = socket;
        this.clientName = clientName;
        this.clientID = generateClientID();
        initializeStreams();
    }

    private void tryConnectingToServer() throws IOException {
        try {
            closeResources();
            this.socket = new Socket(hostIp, port);
        } catch (IOException e) {
            throw new IOException("Failed to connect to server", e);
        }
    }
    private void initializeStreams() throws IOException {
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeResources();
            throw new IOException("Failed to initialize client streams", e);
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        if (clientName == null || clientName.isEmpty()) {
            throw new IllegalArgumentException("Client name cannot be null or empty");
        }
        this.clientName = clientName;
    }

    public String getClientID() {
        return clientID;
    }
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean isConnectedToServer() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public Object receiveMessageOld() throws IOException, ClassNotFoundException {
        try {
            return inputStream.readObject();
        } catch (SocketTimeoutException e) {
            logger.error("Socket read timeout: ", e);
            return null;
        }
    }

    public GameUpdate receiveMessage() throws IOException, ClassNotFoundException {
        try {
            socket.setSoTimeout(5000);  // Optional: Set timeout
            return internalReceiveMessage();
        } catch (SocketTimeoutException e) {
            logger.error("Socket read timeout: ", e);
            return null;
        } finally {
            socket.setSoTimeout(0);
        }
    }

    private GameUpdate internalReceiveMessage() throws ClassNotFoundException, IOException {
        Object message = inputStream.readObject();
        if (message instanceof GameUpdate gameUpdate) {
            gameUpdate.setReceivedTime(LocalDateTime.now());
            return gameUpdate;
        } else {
            throw new ClassNotFoundException("Unexpected object received of type: " + message.getClass().getName());
        }
    }

    public void sendMessage(GameUpdate message) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        message.setBroadcastTime(LocalDateTime.now());
        outputStream.writeObject(message);
        outputStream.flush();
    }

    public void sendMessageOld(Object message) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        outputStream.writeObject(message);
        outputStream.flush();
    }

    public void closeResources() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            logger.debug(CLOSED_EVERYTHING_MSG);
        } catch (IOException e) {
            logger.error(ERROR_CLOSING_RESOURCES_MSG, e);
        }
    }

    public void reconnect() throws IOException {
        closeResources();
        tryConnectingToServer();
        initializeStreams();
    }

    private String generateClientID() {
        return UUID.randomUUID().toString();
    }
}
