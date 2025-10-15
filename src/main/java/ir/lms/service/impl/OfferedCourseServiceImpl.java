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
import java.time.LocalDate;
import java.util.List;

@Service
public class OfferedCourseServiceImpl extends BaseServiceImpl<OfferedCourse, Long> implements OfferedCourseService {
    private final static String NOT_BE_NULL = "Offered course start and end times must not be null";
    private final static String TIME_ILLEGAL = "Offered course start time must be before end time";
    private final static String ILLEGAL_AFTER_START = "Can't %s Offered course after term start date!";
    private final static String TERM_NOT_ALLOWED = "You are not allowed to access this term!";
    private final static String NOT_FOUND = "%s not found!";

    private final AccountRepository accountRepository;
    private final TermRepository termRepository;
    private final OfferedCourseRepository offeredCourseRepository;

    public OfferedCourseServiceImpl(JpaRepository<OfferedCourse, Long> repository,
                                    AccountRepository accountRepository, TermRepository termRepository, OfferedCourseRepository offeredCourseRepository) {
        super(repository);
        this.accountRepository = accountRepository;
        this.termRepository = termRepository;
        this.offeredCourseRepository = offeredCourseRepository;
    }


    @Override
    public OfferedCourse update(Long aLong, OfferedCourse offeredCourse) {
        OfferedCourse foundedOfferedCourse = offeredCourseRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));

        LocalDate termStartDate = foundedOfferedCourse.getTerm().getAcademicCalender().getCourseRegistrationStart();
        if (!termStartDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(ILLEGAL_AFTER_START);
        }
        foundedOfferedCourse.setClassStartTime(offeredCourse.getClassStartTime());
        foundedOfferedCourse.setClassEndTime(offeredCourse.getClassEndTime());
        foundedOfferedCourse.setCapacity(offeredCourse.getCapacity());
        return offeredCourseRepository.save(foundedOfferedCourse);
    }


    @Override
    protected void prePersist(OfferedCourse offeredCourse) {


        if (offeredCourseRepository.existsOverlappingCourse(offeredCourse.getTeacher(), offeredCourse.getClassStartTime(), offeredCourse.getDayOfWeek(), offeredCourse.getClassEndTime())) {
            throw new AccessDeniedException("Over lapping course!");
        }
        if (offeredCourse.getClassStartTime() == null || offeredCourse.getClassEndTime() == null) {
            throw new IllegalArgumentException(NOT_BE_NULL);
        }
        if (!offeredCourse.getClassStartTime().isBefore(offeredCourse.getClassEndTime())) {
            throw new IllegalArgumentException(TIME_ILLEGAL);
        }
        offeredCourse.setCourseStatus(CourseStatus.UNFILLED);
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
