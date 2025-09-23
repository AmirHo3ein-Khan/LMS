package ir.lms.service.impl;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.exception.OptionSizeException;
import ir.lms.model.*;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.ExamQuestionRepository;
import ir.lms.repository.ExamRepository;
import ir.lms.repository.QuestionRepository;
import ir.lms.service.QuestionService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.QuestionFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class QuestionServiceImpl extends BaseServiceImpl<Question, Long> implements QuestionService  {

    private final QuestionFactory questionFactory;
    private final AccountRepository accountRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    protected QuestionServiceImpl(JpaRepository<Question, Long> repository, QuestionFactory questionFactory, AccountRepository accountRepository, ExamRepository examRepository, QuestionRepository questionRepository, ExamQuestionRepository examQuestionRepository) {
        super(repository);
        this.questionFactory = questionFactory;
        this.accountRepository = accountRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examQuestionRepository = examQuestionRepository;
    }
    public Question createQuestion(String type , Question question , List<Option> options) {
        Question q = questionFactory.createQuestion(type, question , options);
        return persist(q);
    }

    @Override
    protected void prePersist(Question question) {
        if (question.getTitle() == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        if (question.getQuestionText() == null) {
            throw new IllegalArgumentException("Question text cannot be null");
        }
        if (question.getDefaultScore() == null) {
            throw new IllegalArgumentException("Default score cannot be null");
        }
    }

    @Override
    public void assignQuestionToExam(Long examId, Long questionId , Double score) {
        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found!"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found!"));

        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setQuestionScore(score);

        examQuestionRepository.save(examQuestion);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }


    public void calculateTotalScore(ExamTemplate exam) {
        double totalScore = 0.0;
        for (ExamQuestion examQuestion : exam.getExamQuestions()) {
            Double score = examQuestion.getQuestionScore();
            if (score == null) {
                score = examQuestion.getQuestion().getDefaultScore();
            }
            totalScore += score != null ? score : 0.0;
        }
        exam.setExamScore(totalScore);
    }
}
