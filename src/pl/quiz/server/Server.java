package pl.quiz.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private int port;
    private final int threadNumber = 250;
    private volatile boolean isRunning = true;
    private ServerSocket serverSocket;
    private List<QuizClient> clients = new ArrayList<>();
    private ExecutorService executorService;

    public Server(int port) {
        this.port = port;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.executorService = Executors.newFixedThreadPool(threadNumber);
        printServerLogs("Server created successfully");
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                handleClient(socket);
            } catch (SocketException e) {
                printServerLogs("Closing socket");
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    private void handleClient(Socket socket) throws IOException {
        QuizClient quizClient = new QuizClient(socket);
        clients.add(quizClient);
        printServerLogs("Client with uuid " + quizClient.getUuid() + " joined to server.");
        ClientHandler clientHandler = new ClientHandler(quizClient);
        executorService.submit(clientHandler);
    }

    public void stopServer() throws IOException {
        this.isRunning = false;
        clients.forEach(QuizClient::deleteClient);
        serverSocket.close();
        printServerLogs("Server successfully stopped");
    }

    private void printServerLogs(String log) {
        System.out.println("\033[1;92m" + log + "\033[0m");
    }
}
