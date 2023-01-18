package pl.quiz.client;

import pl.quiz.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String ip = "localhost";
    private int port = 8080;
    private volatile boolean isRunning = true;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        Client client = new Client();
        while (true) {
            try {
                client.startConnection();
                break;
            } catch (ConnectException e) {
                System.err.println("Server is not ready yet");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
        new Thread(() -> {
            while (true) {
                try {
                    String serverInput = client.in.readLine();
                    System.out.println(serverInput);
                    if (serverInput != null && serverInput.startsWith("score")) {
                        client.stopConnection();
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("exception");
                }
            }
        }).start();
        String userInput;
        while ((userInput = scanner.nextLine()) != null) {
            if (client.isRunning) {
                client.out.println(userInput);
            } else {
                System.exit(0);
            }
        }
    }

    public void startConnection() throws IOException {
        clientSocket = new Socket(this.ip, this.port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        isRunning = false;
    }
}
