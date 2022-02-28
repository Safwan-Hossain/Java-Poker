import java.io.*;
import java.net.Socket;

public class Client {
    private Player thisPlayer;
    private String playerName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public Client(Socket socket, String name) {
        this.socket = socket;
        this.playerName = name;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, outputStream, inputStream);
        }
    }

    private void closeEverything(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

