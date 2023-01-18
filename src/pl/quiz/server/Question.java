package pl.quiz.server;

import java.util.Map;
import java.util.stream.Collectors;

public record Question(String question, Map<Character, String> answers, Character validAnswer) {
    @Override
    public String toString() {
        return question + '\n' +
                answers.entrySet().stream().map(x -> x.getKey().toString() + ". " + x.getValue()).collect(Collectors.joining("\n"));
    }

}
