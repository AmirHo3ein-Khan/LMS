package ir.lms.util.factory;

import ir.lms.model.Answer;
import ir.lms.model.DescriptiveAnswer;
import ir.lms.model.Option;
import ir.lms.model.TestAnswer;
import org.springframework.stereotype.Component;

@Component
public class AnswerFactory {

    public Answer createAnswer(String type , Answer answer, Option option, String answerText) {
        return switch (type.toUpperCase()) {
            case "DESCRIPTIVE" -> DescriptiveAnswer.builder()
                    .answerText(answerText)
                    .examQuestion(answer.getExamQuestion())
                    .examInstance(answer.getExamInstance())
                    .build();
            case "TEST" -> TestAnswer.builder()
                    .option(option)
                    .score(answer.getScore())
                    .examQuestion(answer.getExamQuestion())
                    .examInstance(answer.getExamInstance())
                    .build();
            default -> throw new IllegalArgumentException("Unknown question type: " + type);
        };
    }
}
