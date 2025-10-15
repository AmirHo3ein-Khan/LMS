package ir.lms.service.impl;

import ir.lms.model.*;
import ir.lms.repository.*;
import ir.lms.service.GradingService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GradingServiceImpl implements GradingService {
    private final ExamInstanceRepository examInstanceRepository;
    private final ExamRepository examRepository;
    private final PersonRepository personRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public GradingServiceImpl(ExamInstanceRepository examInstanceRepository,
                              ExamRepository examRepository, PersonRepository personRepository, ExamQuestionRepository examQuestionRepository, AnswerRepository answerRepository, QuestionRepository questionRepository) {
        this.examInstanceRepository = examInstanceRepository;
        this.personRepository = personRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void autoTestGrading(Long examId, Long studentId) {
        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam Instance Not Found"));

        Person person = personRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Person Not Found"));

        ExamInstance examInstance = examInstanceRepository.findByPersonAndExam(person, exam)
                .orElseThrow(() -> new RuntimeException("Exam Instance Not Found"));

        if (examInstance.getAnswers() != null) {
        List<Answer> answers = examInstance.getAnswers();
        for (Answer answer : answers) {
            if (answer instanceof TestAnswer) {
                if (((TestAnswer) answer).getOption().isCorrect()) {
                    ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, answer.getExamQuestion().getQuestion())
                            .orElseThrow(() -> new RuntimeException("Question Not Found"));
                    Double questionScore = examQuestion.getQuestionScore();
                    if (questionScore == null) {
                        questionScore = examQuestion.getQuestion().getDefaultScore();
                    }
                    answer.setScore(questionScore);
                } else {
                    answer.setScore(0.0);
                }
        }
                answerRepository.save(answer);
                double score = answer.getScore();
                examInstance.setTotalScore(examInstance.getTotalScore() + score);
            }
        }
        examInstanceRepository.save(examInstance);
    }

    @Override
    public void descriptiveGrading(Long examId, Long studentId, Long questionId, Double score) {
        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam Instance Not Found"));

        Person person = personRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Person Not Found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question Not Found"));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new RuntimeException("Question Not Found"));

        ExamInstance examInstance = examInstanceRepository.findByPersonAndExam(person, exam)
                .orElseThrow(() -> new RuntimeException("Exam Instance Not Found"));

        Answer answer = answerRepository.findByExamQuestionAndExamInstance(examQuestion, examInstance)
                .orElseThrow(() -> new RuntimeException("Answer Not Found"));

        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
        answer.setScore(score);
        answerRepository.save(answer);

        examInstance.setTotalScore(examInstance.getTotalScore() + score);
        examInstanceRepository.save(examInstance);
    }
}
