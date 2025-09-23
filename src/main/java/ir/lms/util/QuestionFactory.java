package ir.lms.util;

import ir.lms.model.DescriptiveQuestion;
import ir.lms.model.Option;
import ir.lms.model.Question;
import ir.lms.model.TestQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionFactory {

    public Question createQuestion(String type, Question question , List<Option> options) {
        switch (type.toUpperCase()) {
            case "TEST":
                TestQuestion tq = TestQuestion.builder()
                        .title(question.getTitle())
                        .questionText(question.getQuestionText())
                        .defaultScore(question.getDefaultScore())
                        .course(question.getCourse())
                        .options(options)
                        .build();
                if (tq.getOptions() != null) {
                    for (Option o : tq.getOptions()) {
                        o.setQuestion(tq);
                    }
                }
                return tq;
            case "DESCRIPTIVE":
                return DescriptiveQuestion.builder()
                        .title(question.getTitle())
                        .questionText(question.getQuestionText())
                        .defaultScore(question.getDefaultScore())
                        .course(question.getCourse())
                        .build();
            default:
                throw new IllegalArgumentException("Unknown question type: " + type);
        }
    }
}
