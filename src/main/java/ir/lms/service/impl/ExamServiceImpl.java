package ir.lms.service.impl;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.ExamQuestion;
import ir.lms.model.ExamTemplate;
import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.model.enums.ExamState;
import ir.lms.repository.ExamRepository;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.service.ExamService;
import ir.lms.service.OfferedCourseService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExamServiceImpl extends BaseServiceImpl<ExamTemplate, Long> implements ExamService {

    private final static String NOT_BE_NULL = "exam start and end times must not be null";
    private final static String FUTURE_ILLEGAL = "exam %s time must be in the future";
    private final static String TIME_ILLEGAL = "exam start time must be before end time";
    private final static String ILLEGAL_AFTER_START = "Can't %s exam after term start date!";
    private final static String NOT_FOUND = "%s not found!";

    private final ExamRepository examRepository;
    private final OfferedCourseRepository offeredCourseRepository;

    protected ExamServiceImpl(JpaRepository<ExamTemplate, Long> repository, ExamRepository examRepository, OfferedCourseRepository offeredCourseRepository) {
        super(repository);
        this.examRepository = examRepository;
        this.offeredCourseRepository = offeredCourseRepository;
    }

    @Override
    protected void prePersist(ExamTemplate examTemplate) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.toInstant(ZoneOffset.UTC);
        if (examTemplate.getExamStartTime() == null || examTemplate.getExamEndTime() == null) {
            throw new IllegalArgumentException(NOT_BE_NULL);
        }
        if (!examTemplate.getExamStartTime().isAfter(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "start"));
        }
        if (!examTemplate.getExamEndTime().isAfter(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "end"));
        }
        if (!examTemplate.getExamStartTime().isBefore(examTemplate.getExamEndTime())) {
            throw new IllegalArgumentException(TIME_ILLEGAL);
        }

        Instant today = Instant.now();
        examTemplate.setDeleted(false);

        if (examTemplate.getExamStartTime().isAfter(today)) {
            examTemplate.setExamState(ExamState.NOT_STARTED);

        } else if (examTemplate.getExamStartTime().equals(today)) {
            examTemplate.setExamState(ExamState.STARTED);

        } else {
            examTemplate.setExamState(ExamState.FINISHED);
        }
    }

    @Override
    protected void preUpdate(ExamTemplate examTemplate) {
        Instant examTemplateStartDate = examTemplate.getExamStartTime();
        if (!examTemplateStartDate.isAfter(Instant.now())) {
            throw new IllegalArgumentException(ILLEGAL_AFTER_START);
        }
    }

    @Override
    protected void preDelete(Long aLong) {
        ExamTemplate examTemplate = examRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Exam")));
        examTemplate.setDeleted(true);
        examRepository.save(examTemplate);
    }

    @Override
    public List<ExamTemplate> findAllExamOfACourse(Long courseId) {
        OfferedCourse offeredCourse = offeredCourseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));
        return offeredCourse.getExamTemplates();
    }

    @Override
    public ExamTemplate findById(Long aLong) {
        ExamTemplate examTemplate = examRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Exam")));
        if (examTemplate.isDeleted()) {
            throw new EntityNotFoundException(String.format(NOT_FOUND, "Exam"));
        }
        return examTemplate;
    }

    @Override
    public List<ExamTemplate> findAll() {
        List<ExamTemplate> examTemplates = examRepository.findAll();
        List<ExamTemplate> result = new ArrayList<>();
        for (ExamTemplate examTemplate : examTemplates) {
            if (!examTemplate.isDeleted()) {
                result.add(examTemplate);
            }
        }
        return result;
    }
}
