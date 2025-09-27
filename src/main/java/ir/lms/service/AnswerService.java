package ir.lms.service;

import ir.lms.model.Answer;
import ir.lms.model.Option;
import ir.lms.service.base.BaseService;

import java.security.Principal;

public interface AnswerService extends BaseService<Answer , Long> {
    void saveAnswer(String type,Answer answer , Option option , String answerText);
    Option findOptionById(Long id);
}
