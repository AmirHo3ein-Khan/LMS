package ir.lms.mapper;

import ir.lms.dto.answer.AnswerDTO;
import ir.lms.dto.course.CourseDTO;
import ir.lms.mapper.base.BaseMapper;
import ir.lms.model.*;
import ir.lms.repository.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;

@Mapper(componentModel = "spring")
public abstract class AnswerMapper {


    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamInstanceRepository examInstanceRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public abstract AnswerDTO toDto(Answer entity);

    public abstract Answer toEntity(AnswerDTO dto, @Context Principal principal);

    @AfterMapping
    protected void afterToEntity(AnswerDTO dto, @MappingTarget Answer entity , @Context Principal principal) {

        ExamTemplate examTemplate = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("Exam id " + dto.getExamId() + " not found."));

        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Username " + principal.getName() + " not found."));

        ExamInstance examInstance = examInstanceRepository.findByPersonAndExam(account.getPerson(), examTemplate)
                .orElseThrow(() -> new IllegalArgumentException("Exam id " + dto.getExamId() + " not found."));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question id " + dto.getQuestionId() + " not found."));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(examTemplate, question)
                .orElseThrow(() -> new IllegalArgumentException("Exam id " + dto.getExamId() + " not found."));

        entity.setExamInstance(examInstance);
        entity.setExamQuestion(examQuestion);
    }
}
