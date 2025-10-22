package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.exception.ExamExpiredException;
import ir.lms.exception.ExamNotStartedException;
import ir.lms.model.*;
import ir.lms.model.enums.ExamInstanceStatus;
import ir.lms.model.enums.ExamState;
import ir.lms.repository.*;
import ir.lms.service.ExamService;
import ir.lms.service.GradingService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamServiceImpl extends BaseServiceImpl<ExamTemplate, Long> implements ExamService {

    private final static String NOT_BE_NULL = "exam start and end times must not be null";
    private final static String FUTURE_ILLEGAL = "exam %s time must be in the future";
    private final static String TIME_ILLEGAL = "exam start time must be before end time";
    private final static String ILLEGAL_AFTER_START = "Can't update exam after term start date!";
    private final static String NOT_FOUND = "%s not found!";

    private final ExamRepository examRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final PersonRepository personRepository;
    private final ExamInstanceRepository examInstanceRepository;
    private final AccountRepository accountRepository;
    private final GradingService gradingService;

    protected ExamServiceImpl(JpaRepository<ExamTemplate, Long> repository, ExamRepository examRepository,
                              OfferedCourseRepository offeredCourseRepository, PersonRepository personRepository,
                              ExamInstanceRepository examInstanceRepository, AccountRepository accountRepository, GradingService gradingService) {
        super(repository);
        this.examRepository = examRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.personRepository = personRepository;
        this.examInstanceRepository = examInstanceRepository;
        this.accountRepository = accountRepository;
        this.gradingService = gradingService;
    }

    @Override
    protected void prePersist(ExamTemplate examTemplate) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.toInstant(ZoneOffset.UTC);
        if (examTemplate.getExamStartTime() == null || examTemplate.getExamEndTime() == null) {
            throw new IllegalArgumentException(NOT_BE_NULL);
        }
        if (examTemplate.getExamStartTime().isBefore(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "start"));
        }
        if (examTemplate.getExamEndTime().isBefore(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "end"));
        }
        if (examTemplate.getExamStartTime().isAfter(examTemplate.getExamEndTime())) {
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
    public void startExam(Long examId, Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AccessDeniedException(NOT_BE_NULL));

        Person person = personRepository.findById(account.getPerson().getId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Person")));

        ExamTemplate examTemplate = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Exam")));

        Optional<ExamInstance> foundedExamInstanceByPersonAndExam = examInstanceRepository.findByPersonAndExam(person, examTemplate);
        //fixme: is the exam started can student start it again before complete it
        if (foundedExamInstanceByPersonAndExam.isPresent() && foundedExamInstanceByPersonAndExam.get().getStatus().equals(ExamInstanceStatus.COMPLETED)) {
            throw new AccessDeniedException("You already complete this exam!");
        }

        if (personRepository.existsByIdAndOfferedCourses_Id(account.getPerson().getId(), examTemplate.getOfferedCourse().getId())) {
            if (examTemplate.getExamState().equals(ExamState.STARTED)) {
                LocalDateTime now = LocalDateTime.now();
                ExamInstance examInstance = ExamInstance.builder()
                        .exam(examTemplate)
                        .person(person)
                        .startAt(now)
                        .status(ExamInstanceStatus.IN_PROGRESS)
                        .totalScore(0)
                        .build();

                examInstanceRepository.save(examInstance);
            } else if (examTemplate.getExamState().equals(ExamState.NOT_STARTED)) {
                throw new ExamNotStartedException("Exam not start yet!");
            } else if (examTemplate.getExamState().equals(ExamState.FINISHED)) {
                throw new ExamExpiredException("Exam time is expired");
            }
        } else {
            throw new AccessDeniedException("You don't have the course for access to start this exam!");
        }
    }

    @Override
    public void submitExam(Long examId, Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new AccessDeniedException(NOT_BE_NULL));

        ExamTemplate exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + examId));

        ExamInstance studentExam = examInstanceRepository.findByPersonAndExam(account.getPerson(), exam)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found for this student"));

        if (studentExam.getStatus() == ExamInstanceStatus.COMPLETED) {
            throw new AccessDeniedException("You have already submitted this exam!");
        }
        studentExam.setStatus(ExamInstanceStatus.COMPLETED);
        studentExam.setEndAt(LocalDateTime.now());

        gradingService.autoTestGrading(examId, account.getPerson().getId());

        examInstanceRepository.save(studentExam);
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
        return examRepository.findByDeletedIsFalse();
    }

    @Override
    public ExamTemplate update(Long aLong, ExamTemplate examTemplate) {
        ExamTemplate foundedExam = examRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Exam")));

        Instant examTemplateStartDate = foundedExam.getExamStartTime();
        if (!examTemplateStartDate.isAfter(Instant.now())) {
            throw new IllegalArgumentException(ILLEGAL_AFTER_START);
        }

        foundedExam.setTitle(examTemplate.getTitle());
        foundedExam.setDescription(examTemplate.getDescription());
        foundedExam.setExamState(examTemplate.getExamState());
        foundedExam.setExamStartTime(examTemplateStartDate);
        foundedExam.setExamEndTime(examTemplateStartDate);
        return examRepository.save(foundedExam);
    }
}
