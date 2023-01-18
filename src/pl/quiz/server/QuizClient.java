package pl.quiz.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class QuizClient implements Runnable {
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private UUID uuid;
    private int score = 0;

    public QuizClient(Socket socket) throws IOException {
        this.socket = socket;
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.uuid = UUID.randomUUID();
    }

    @Override
    public void run() {
        System.out.println("run");
    }

    public void deleteClient() {
        try {
            printWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error during stopping server");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addPoint() {
        score++;
    }

    public int getScore() {
        return score;
    }
}
