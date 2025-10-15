package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.CourseHasNotLimitException;
import ir.lms.exception.CourseRegisterDateException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.AcademicCalender;
import ir.lms.model.Account;
import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.service.StudentService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class StudentServiceImpl implements StudentService {
    private final static String NOT_FOUND = "%s not found!";
    private final static String NOT_ACCESS_TO_COURSE = "This course is not in your major to take!";
    private final static String UNIT_LIMITED = "Could not take the course (Unit limited)!";


    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final OfferedCourseRepository offeredCourseRepository;

    public StudentServiceImpl(PersonRepository personRepository, AccountRepository accountRepository, OfferedCourseRepository offeredCourseRepository) {
        this.accountRepository = accountRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.personRepository = personRepository;
    }

    @Override
    public void studentTakeCourse(Long courseId, Principal principal) {

        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Account")));

        Person person = account.getPerson();

        OfferedCourse offeredCourse = offeredCourseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));

        AcademicCalender academicCalender = offeredCourse.getTerm().getAcademicCalender();

        LocalDate now = LocalDate.now();

        if (academicCalender.getCourseRegistrationStart().isBefore(now)) {
            throw new CourseRegisterDateException("Course register date not started!");
        }
        if (academicCalender.getCourseRegistrationEnd().isAfter(now)) {
            throw new CourseRegisterDateException("Course register date ended!");
        }

        if (person.getOfferedCourses() != null) {
            Integer totalUnits = offeredCourseRepository.getTotalUnitsByPersonId(person.getId());
            if (totalUnits + offeredCourse.getCourse().getUnit() <= 20) {
                throw new IllegalArgumentException(UNIT_LIMITED);
            }
        }
        Integer capacity = offeredCourse.getCapacity();
        if (capacity <= 0) {
            throw new CourseHasNotLimitException("The course has no limit!");
        }

        if (!(offeredCourse.getTerm().getMajor() == person.getMajor())) {
            throw new AccessDeniedException(NOT_ACCESS_TO_COURSE);
        }

        offeredCourse.setCapacity(offeredCourse.getCapacity() - 1);

        offeredCourse.setStudents(new ArrayList<>());
        offeredCourse.getStudents().add(person);
        offeredCourseRepository.save(offeredCourse);

        person.setOfferedCourses(new ArrayList<>());
        person.getOfferedCourses().add(offeredCourse);
        personRepository.save(person);

    }
}
