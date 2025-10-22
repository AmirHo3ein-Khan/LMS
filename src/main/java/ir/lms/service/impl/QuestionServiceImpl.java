package ir.lms.service.impl;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.*;
import ir.lms.repository.*;
import ir.lms.service.QuestionService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.factory.QuestionFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class QuestionServiceImpl extends BaseServiceImpl<Question, Long> implements QuestionService {

    private final QuestionFactory questionFactory;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final CourseRepository courseRepository;

    protected QuestionServiceImpl(JpaRepository<Question, Long> repository,
                                  QuestionFactory questionFactory, ExamRepository examRepository,
                                  QuestionRepository questionRepository, ExamQuestionRepository examQuestionRepository,
                                  CourseRepository courseRepository) {
        super(repository);
        this.questionFactory = questionFactory;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.courseRepository = courseRepository;
    }

    public Question createQuestion(String type, Question question, List<Option> options) {
        Question q = questionFactory.createQuestion(type, question, options);
        return persist(q);
    }

    @Override
    public Question update(Long aLong, Question question) {
        Question foundedQuestion = questionRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        foundedQuestion.setTitle(question.getTitle());
        foundedQuestion.setQuestionText(question.getQuestionText());
        foundedQuestion.setDefaultScore(question.getDefaultScore());
        return questionRepository.save(foundedQuestion);
    }

    @Override
    protected void prePersist(Question question) {
        if (question.getTitle() == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        if (question.getQuestionText() == null) {
            throw new IllegalArgumentException("Question text cannot be null");
        }
        if (question.getDefaultScore() == 0) {
            throw new IllegalArgumentException("Default score cannot be null");
        }
    }

    @Override
    public void assignQuestionToExam(Long examId, Long questionId, Double score) {
        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found!"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found!"));

        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        if (score == 0) examQuestion.setQuestionScore(question.getDefaultScore());
        else examQuestion.setQuestionScore(score);


        examQuestionRepository.save(examQuestion);
        questionRepository.save(question);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }

    @Override
    public List<Question> findQuestionsByExamId(Long examId) {
        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found!"));
        if (!exam.isDeleted()) {
            return questionRepository.findByExamQuestions_Exam_Id(examId);
        }
        throw new EntityNotFoundException("Exam not found!");
    }

    @Override
    public List<Question> findQuestionsOfCourse(Long courseId, Principal principal) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found!"));
        questionRepository.findByCourse_Id(courseId);
        if (!course.isDeleted()) {
            return questionRepository.findByCourse_Id(courseId);
        }
        throw new EntityNotFoundException("Course not found!");
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
