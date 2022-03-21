import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String clientID;
    private String playerName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.playerName = name;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        sendMessage(playerName);
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean IsConnectedToServer() {
        return socket.isConnected();
    }

    public Object listenForMessage() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public void sendMessage(Object object) throws IOException {
        outputStream.writeObject(object);
        outputStream.flush();
    }

    private void closeEverything() {
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
            System.out.println("CLOSED EVERYTHING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO TEMPORARY CODE
    private static String GetValidUsername() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        while (username.strip().isBlank()) {
            System.out.print("Please print your username: ");
            username = scanner.nextLine();
        }
        scanner.close();
        return username;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String username = GetValidUsername();
        Socket socket = new Socket(InetAddress.getLocalHost(), 100);
        Client client = new Client(socket, username);
        client.listenForMessage();
        //client.performAction();
    }
}

