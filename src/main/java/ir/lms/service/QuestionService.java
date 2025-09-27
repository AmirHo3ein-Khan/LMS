package ir.lms.service;

import ir.lms.model.Option;
import ir.lms.model.Question;
import ir.lms.service.base.BaseService;

import java.util.List;

public interface QuestionService extends BaseService<Question, Long> {
    Question createQuestion(String type , Question question , List<Option> options);
    void assignQuestionToExam(Long examId, Long questionId, Double score);
    List<Question> findQuestionsByExamId(Long examId);
}
