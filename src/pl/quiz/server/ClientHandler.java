package pl.quiz.server;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {

    private static QuizRepository quizRepository = new QuizRepository();
    private QuizClient client;

    public ClientHandler(QuizClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        Map<Question, String> answers = new HashMap<>();
        try {
            List<Question> questions = quizRepository.readQuizFile();
            client.getPrintWriter().println("Your uuid: " + client.getUuid());
            for (int i = 5; i > 0; i--) {
                client.getPrintWriter().println("Starting quiz in " + i + " seconds");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            for (Question question : questions) {
                try {
                    client.getPrintWriter().println(question.toString());
                    client.getSocket().setSoTimeout(60 * 1000);
                    String response = client.getBufferedReader().readLine();
                    answers.put(question, response.toUpperCase());
                    if (question.validAnswer().toString().equals(response)) {
                        client.addPoint();
                    }
                } catch (SocketTimeoutException e) {
                    answers.put(question, "No answer");
                }
            }
            client.getPrintWriter().println("score " + client.getScore() + "/" + questions.size());

        } catch (SocketException e) {
            System.err.println("Client " + client.getUuid() + " left quiz");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            quizRepository.saveClientScore(client.getUuid(), client.getScore(), answers);
        }
    }
}
