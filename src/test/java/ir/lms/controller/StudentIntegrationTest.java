package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.model.*;
import ir.lms.model.enums.*;
import ir.lms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private OfferedCourseRepository offeredCourseRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private TestQuestionRepository testQuestionRepository;
    @Autowired private DescriptiveQuestionRepository descriptiveQuestionRepository;
    @Autowired private ExamQuestionRepository examQuestionRepository;
    @Autowired private ExamInstanceRepository examInstanceRepository;
    @Autowired private TestAnswerRepository testAnswerRepository;
    @Autowired private DescriptiveAnswerRepository descriptiveAnswerRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String accessToken;
    private Person student;
    private Course course;
    private OfferedCourse offeredCourse;

    @BeforeEach
    void setUp() throws Exception {
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        student = createPerson("Student", "Student", studentRole, major);
        Account studentAccount = createAccount(student, studentRole);
        this.accessToken = loginAndGetToken(studentAccount.getUsername(), student.getNationalCode());

        course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPerson("Teacher", "Teacher", teacherRole, major);
        createAccount(teacher, teacherRole);

        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(term, course);
        student.setOfferedCourses(new ArrayList<>(List.of(offeredCourse)));
        personRepository.save(student);
    }





    // ---------------- Helper Methods ----------------

    private Person createPerson(String firstName, String lastName, Role role, Major major) {
        Person p = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>(List.of(role)))
                .major(major)
                .build();
        return personRepository.save(p);
    }

    private Account createAccount(Person person, Role activeRole) {
        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(activeRole)
                .build();
        person.setAccount(account);
        return accountRepository.save(account);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        AuthRequestDTO authRequest = AuthRequestDTO.builder().username(username).password(password).build();
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, AuthenticationResponse.class).getAccessToken();
    }

    private Course createCourse(String title, Major major) {
        Course course = Course.builder()
                .title(title)
                .description("Course Description")
                .major(major)
                .build();
        return courseRepository.save(course);
    }

    private Term createTerm(Major major, AcademicCalender academicCalender, Semester semester) {
        Term term = Term.builder().year(2025).academicCalender(academicCalender).semester(semester).major(major).build();
        return termRepository.save(term);
    }

    private AcademicCalender createCalender(LocalDate courseRegistrationStart,
                                            LocalDate courseRegistrationEnd,
                                            LocalDate classesStartDate,
                                            LocalDate classesEndDate) {
        return AcademicCalender.builder()
                .courseRegistrationStart(courseRegistrationStart)
                .courseRegistrationEnd(courseRegistrationEnd)
                .classesStartDate(classesStartDate)
                .classesEndDate(classesEndDate)
                .build();
    }

    private OfferedCourse createOfferedCourse(Term term, Course course) {
        OfferedCourse oc = OfferedCourse.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .build();
        return offeredCourseRepository.save(oc);
    }

    private ExamTemplate createExam(OfferedCourse offeredCourse) {
        ExamTemplate exam = ExamTemplate.builder()
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .offeredCourse(offeredCourse)
                .examState(ExamState.STARTED)
                .title("Exam Title")
                .description("Exam Description")
                .deleted(false)
                .build();
        offeredCourse.setExamTemplates(new ArrayList<>(List.of(exam)));
        offeredCourseRepository.save(offeredCourse);
        return examRepository.save(exam);
    }

    private TestQuestion createTestQuestion(Course course) {
        List<Option> options = new ArrayList<>(Arrays.asList(
                Option.builder().optionText("opt 1").correct(false).build(),
                Option.builder().optionText("opt 2").correct(true).build(),
                Option.builder().optionText("opt 3").correct(false).build(),
                Option.builder().optionText("opt 4").correct(false).build()
        ));
        TestQuestion q = TestQuestion.builder()
                .title("Question Title")
                .questionText("Question Text")
                .course(course)
                .options(options)
                .defaultScore(2)
                .build();
        return testQuestionRepository.save(q);
    }

    private DescriptiveQuestion createDescriptiveQuestion(Course course) {
        DescriptiveQuestion q = DescriptiveQuestion.builder()
                .title("Descriptive Question")
                .questionText("Question Text")
                .course(course)
                .defaultScore(2)
                .build();
        return descriptiveQuestionRepository.save(q);
    }

    private ExamInstance createExamInstance(Person student, ExamTemplate exam) {
        ExamInstance instance = ExamInstance.builder()
                .startAt(LocalDateTime.now())
                .endAt(LocalDateTime.now().plusHours(2))
                .status(ExamInstanceStatus.IN_PROGRESS)
                .person(student)
                .exam(exam)
                .totalScore(0)
                .build();
        return examInstanceRepository.save(instance);
    }

    private static String randomPhone() {
        StringBuilder sb = new StringBuilder("09");
        for (int i = 0; i < 9; i++) sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        return sb.toString();
    }

    private static String randomNationalCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        return sb.toString();
    }
}
