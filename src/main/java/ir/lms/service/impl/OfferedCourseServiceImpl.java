package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.*;
import ir.lms.model.enums.CourseStatus;
import ir.lms.repository.*;
import ir.lms.service.OfferedCourseService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class OfferedCourseServiceImpl extends BaseServiceImpl<OfferedCourse, Long> implements OfferedCourseService {
    private final static String NOT_BE_NULL = "Offered course start and end times must not be null";
    private final static String FUTURE_ILLEGAL = "Offered course %s time must be in the future";
    private final static String TIME_ILLEGAL = "Offered course start time must be before end time";
    private final static String ILLEGAL_AFTER_START = "Can't %s Offered course after term start date!";
    private final static String TERM_NOT_ALLOWED = "You are not allowed to access this term!";
    private final static String NOT_FOUND = "%s not found!";

    private final AccountRepository accountRepository;
    private final OfferedCourseRepository offeredCourseRepository;
    private final TermRepository termRepository;

    public OfferedCourseServiceImpl(JpaRepository<OfferedCourse, Long> repository,
                                    AccountRepository accountRepository, OfferedCourseRepository offeredCourseRepository, TermRepository termRepository) {
        super(repository);
        this.accountRepository = accountRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.termRepository = termRepository;
    }


    @Override
    protected void prePersist(OfferedCourse offeredCourse) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.toInstant(ZoneOffset.UTC);
        if (offeredCourse.getStartTime() == null || offeredCourse.getEndTime() == null) {
            throw new IllegalArgumentException(NOT_BE_NULL);
        }

        if (!offeredCourse.getStartTime().isAfter(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "start"));
        }

        if (!offeredCourse.getEndTime().isAfter(instant)) {
            throw new IllegalArgumentException(String.format(FUTURE_ILLEGAL, "end"));
        }

        if (!offeredCourse.getStartTime().isBefore(offeredCourse.getEndTime())) {
            throw new IllegalArgumentException(TIME_ILLEGAL);
        }
        offeredCourse.setCourseStatus(CourseStatus.UNFILLED);
    }

    @Override
    protected void preUpdate(OfferedCourse offeredCourse) {
        LocalDate termStartDate = offeredCourse.getTerm().getStartDate();
        if (!termStartDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(ILLEGAL_AFTER_START);
        }
    }


    @Override
    public List<OfferedCourse> findAllTeacherCourse(Principal principal) {
        String username = principal.getName();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));
        Person person = account.getPerson();
        return person.getOfferedCourses();
    }


    @Override
    public List<OfferedCourse> findAllStudentCourses(Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Account")));

        Person person = account.getPerson();

        return person.getOfferedCourses();
    }


    @Override
    public List<OfferedCourse> findAllTermCourses(Long termId, Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));

        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Term")));

        if (!account.getActiveRole().getName().equals("ADMIN")) {
            if (!account.getPerson().getMajor().getMajorName().equals(term.getMajor().getMajorName())) {
                throw new AccessDeniedException(TERM_NOT_ALLOWED);
            }
        }
        return term.getOfferedCourses();
    }
}
