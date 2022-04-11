import java.io.*;
import java.net.Socket;

public class Client {
    private String clientID;
    private String clientName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(Socket socket, String clientName) throws IOException {
        this.socket = socket;
        this.clientName = clientName;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean isConnectedToServer() {
        return socket.isConnected();
    }

    public Object listenForMessage() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public void sendMessage(Object object) throws IOException {
        outputStream.writeObject(object);
        outputStream.flush();
    }

    public void closeEverything() {
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
            System.out.println("CLIENT: CLOSED EVERYTHING");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

