package ir.lms.service.impl;

import ir.lms.exception.EntityNotFoundException;
import ir.lms.exception.IllegalRequestException;
import ir.lms.model.Account;
import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.OfferedCourseRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.service.StudentService;
import ir.lms.util.dto.ApiResponseDTO;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final PersonRepository personRepository;
    private final AccountRepository accountRepository;
    private final OfferedCourseRepository  offeredCourseRepository;

    public StudentServiceImpl(PersonRepository personRepository, AccountRepository accountRepository, OfferedCourseRepository offeredCourseRepository) {
        this.accountRepository = accountRepository;
        this.offeredCourseRepository = offeredCourseRepository;
        this.personRepository = personRepository;
    }

    @Override
    public void studentGetCourse(Long courseId, Principal principal) {

        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(()->  new EntityNotFoundException("Account not found"));

        Person person = account.getPerson();

        OfferedCourse offeredCourse = offeredCourseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if (!(offeredCourse.getTerm().getMajor() == person.getMajor())) {
            throw new IllegalRequestException("This course not in person major to take!");
        }

        List<Person> students = new ArrayList<>();
        students.add(person);
        offeredCourse.setStudent(students);
        offeredCourseRepository.save(offeredCourse);

        List<OfferedCourse> offeredCourses = new ArrayList<>();
        offeredCourses.add(offeredCourse);
        person.setOfferedCourses(offeredCourses);

        personRepository.save(person);

    }
}
