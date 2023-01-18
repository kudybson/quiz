package pl.quiz.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class QuizRepository {

    private List<Question> cache;
    private static final String QUESTIONS_PATH = "bazaPytan.txt";
    private static final String ANSWERS_PATH = "bazaOdpowiedzi.txt";
    private static final String SCORES_PATH = "wyniki.txt";

    private static ReentrantLock lock = new ReentrantLock();

    public List<Question> readQuizFile() throws IOException {
        if (cache != null) {
            return cache;
        }
        cache = new ArrayList<>();
        String question;
        Map<Character, String> answers;
        Character correct;
        List<String> lines = Files.lines(Path.of(QUESTIONS_PATH)).collect(Collectors.toList());
        if (lines.size() % 6 != 0) {
            System.err.println("Wrong question file format!");
        }
        for (int i = 0; i < lines.size(); i += 6) {
            question = lines.get(i);
            answers = new HashMap<>();
            for (String s : lines.subList(i + 1, i + 5)) {
                String[] split = s.split("\\.");
                answers.put(split[0].charAt(0), split[1]);
            }
            correct = lines.get(i + 5).charAt(0);
            cache.add(new Question(question, answers, correct));
        }
        return cache;
    }

    public void saveClientScore(UUID uuid, int score, Map<Question, String> answers) {
        lock.lock();
        try {
            File fileScores = new File(SCORES_PATH);
            FileWriter fileWriterScores = new FileWriter(fileScores, true);
            BufferedWriter bufferedWriterScores = new BufferedWriter(fileWriterScores);
            bufferedWriterScores.write(uuid.toString() + ": score " + score + "\n");
            bufferedWriterScores.close();
            fileWriterScores.close();

            File fileAnswers = new File(ANSWERS_PATH);
            FileWriter fileWriterAnswers = new FileWriter(fileAnswers, true);
            BufferedWriter bufferedWriterAnswers = new BufferedWriter(fileWriterAnswers);
            bufferedWriterAnswers.write("Answers of user: " + uuid + "\n");
            bufferedWriterAnswers.write(answers.entrySet().stream().map(x -> x.getKey().question() + " - " + x.getValue() + "\n").collect(Collectors.joining()));
            bufferedWriterAnswers.close();
            fileWriterAnswers.close();
        } catch (Exception e) {
            System.err.println("Error during saving score");
        } finally {
            lock.unlock();
        }
    }
}
