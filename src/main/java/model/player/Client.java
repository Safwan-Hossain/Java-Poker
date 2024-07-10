package model.player;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

public class Client {
    private static final String CLOSED_EVERYTHING_MSG = "CLIENT: CLOSED EVERYTHING";
    private static final String ERROR_CLOSING_RESOURCES_MSG = "Error while closing resources: ";

    private String clientID;
    private String clientName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(Socket socket, String clientName) throws IOException {
        if (socket == null || clientName == null || clientName.isEmpty()) {
            throw new IllegalArgumentException("Socket and client name cannot be null or empty");
        }

        this.socket = socket;
        this.clientName = clientName;
        this.clientID = generateClientID();
        initializeStreams();
    }

    private void initializeStreams() throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
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
        return socket != null && socket.isConnected();
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public <T> T receiveMessage(Class<T> clazz) throws IOException, ClassNotFoundException {
        try {
            socket.setSoTimeout(5000);  // Set a timeout for read operations
            Object message = inputStream.readObject();
            return clazz.cast(message);
        } catch (SocketTimeoutException e) {
            System.err.println("Socket read timeout: " + e.getMessage());
            return null;  // Handle timeout as needed
        } catch (ClassCastException e) {
            throw new IOException("Received object is not of expected type", e);
        } finally {
            socket.setSoTimeout(0);  // Reset timeout
        }
    }
    public void sendMessage(Object message) throws IOException {
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
            System.out.println(CLOSED_EVERYTHING_MSG);
        } catch (IOException e) {
            System.err.println(ERROR_CLOSING_RESOURCES_MSG + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reconnect(Socket newSocket) throws IOException {
        closeResources();
        this.socket = newSocket;
        initializeStreams();
    }

    private String generateClientID() {
        return UUID.randomUUID().toString();
    }
}
