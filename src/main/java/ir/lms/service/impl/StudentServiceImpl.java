package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.CourseHasNotLimit;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Account;
import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.service.StudentService;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class StudentServiceImpl implements StudentService {
    private final static String NOT_FOUND = "%s not found!";
    private final static String NOT_ACCESS_TO_COURSE = "This course is not in your major to take!";


    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final OfferedCourseRepository  offeredCourseRepository;

    public StudentServiceImpl(PersonRepository personRepository, AccountRepository accountRepository, OfferedCourseRepository offeredCourseRepository) {
        this.accountRepository = accountRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.personRepository = personRepository;
    }

    @Override
    public void studentTakeCourse(Long courseId, Principal principal) {

        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(()->  new EntityNotFoundException(String.format(NOT_FOUND, "Account")));

        Person person = account.getPerson();

        OfferedCourse offeredCourse = offeredCourseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Course")));

        Integer capacity = offeredCourse.getCapacity();
        if (capacity <= 0) {
            throw new CourseHasNotLimit("The course has no limit!");
        }

        if (!(offeredCourse.getTerm().getMajor() == person.getMajor())) {
            throw new AccessDeniedException(NOT_ACCESS_TO_COURSE);
        }

        offeredCourse.setCapacity(offeredCourse.getCapacity() - 1);

        offeredCourse.getStudent().add(person);
        offeredCourseRepository.save(offeredCourse);


        person.getOfferedCourses().add(offeredCourse);
        personRepository.save(person);

    }
}
