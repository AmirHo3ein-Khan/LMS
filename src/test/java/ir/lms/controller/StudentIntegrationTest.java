package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.dto.answer.AnswerDTO;
import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthenticationResponse;
import ir.lms.model.*;
import ir.lms.model.enums.*;
import ir.lms.repository.*;
import org.aspectj.weaver.loadtime.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private TestQuestionRepository testQuestionRepository;

    @Autowired
    private DescriptiveQuestionRepository descriptiveQuestionRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamInstanceRepository examInstanceRepository;

    @Autowired
    private TestAnswerRepository testAnswerRepository;

    @Autowired
    private DescriptiveAnswerRepository descriptiveAnswerRepository;

    private String accessToken;

    private Person student;

    private OfferedCourse offeredCourse;

    private Course course;


    @BeforeEach
    void beforeEach() throws Exception {
        Role role = roleRepository.findByName("STUDENT").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        student = Person.builder()
                .firstName("student").lastName("student").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).roles(List.of(role)).major(major).build();

        personRepository.save(student);
        Account account = Account.builder()
                .username(student.getPhoneNumber())
                .password(passwordEncoder.encode(student.getNationalCode()))
                .state(RegisterState.ACTIVE).person(student).activeRole(role).build();

        student.setAccount(account);
        accountRepository.save(account);

        AuthRequestDTO build = AuthRequestDTO.builder().username(student.getPhoneNumber()).password(student.getNationalCode()).build();

        String jwtToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthenticationResponse authenticationResponse = objectMapper.readValue(jwtToken, AuthenticationResponse.class);
        this.accessToken = authenticationResponse.getAccessToken();


        course = Course.builder().title("Course Title").description("Course Description")
                .description("Course Description").major(major).build();
        courseRepository.save(course);

        Role teacherRole = roleRepository.findByName("TEACHER").get();

        Person teacher = Person.builder().firstName("teacher").lastName("teacher").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).major(major).roles(List.of(teacherRole)).build();
        personRepository.save(teacher);

        Account teacherAccount = Account.builder().username(teacher.getPhoneNumber())
                .password(passwordEncoder.encode(teacher.getNationalCode()))
                .state(RegisterState.ACTIVE).person(teacher).activeRole(teacherRole).build();
        teacher.setAccount(teacherAccount);
        accountRepository.save(teacherAccount);

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        offeredCourse = OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term).course(course).courseStatus(CourseStatus.UNFILLED)
                .capacity(20).classLocation("Mashhad").build();

        offeredCourseRepository.save(offeredCourse);

        student.setOfferedCourses(List.of(offeredCourse));
        personRepository.save(student);
    }

    @Test
    void studentGetCourse() throws Exception {

        mockMvc.perform(post("/api/student/take/course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void studentStartExam() throws Exception {

        ExamTemplate exam = examRepository.save(ExamTemplate.builder()
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .offeredCourse(offeredCourse)
                .examState(ExamState.STARTED)
                .title("Exam Title").description("Exam Description").deleted(false).build());

        offeredCourse.setExamTemplates(List.of(exam));
        offeredCourseRepository.save(offeredCourse);

        List<Option> options = Arrays.asList(
                Option.builder().optionText("opt 1").correct(false).build(),
                Option.builder().optionText("opt 2").correct(true).build(),
                Option.builder().optionText("opt 3").correct(false).build(),
                Option.builder().optionText("opt 4").correct(false).build());

        TestQuestion q1 = testQuestionRepository.save(TestQuestion.builder()
                .title("Question Title")
                .questionText("Question Text")
                .course(course).options(options)
                .defaultScore(2).build());

        DescriptiveQuestion q2 = descriptiveQuestionRepository.save(DescriptiveQuestion.builder()
                .title("Question Description")
                .questionText("Question Description")
                .course(course).defaultScore(2).build());

        ExamQuestion examQuestion1 = examQuestionRepository.save(ExamQuestion.builder()
                .exam(exam).question(q1).questionScore(5).build());

        ExamQuestion examQuestion2 = examQuestionRepository.save(ExamQuestion.builder()
                .exam(exam).question(q2).questionScore(5).build());


        mockMvc.perform(post("/api/student/start/exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

    }

    @Test
    void studentSubmitExam() throws Exception {
        ExamTemplate exam = examRepository.save(ExamTemplate.builder()
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .offeredCourse(offeredCourse)
                .examState(ExamState.STARTED)
                .title("Exam Title").description("Exam Description").deleted(false).build());

        offeredCourse.setExamTemplates(List.of(exam));
        offeredCourseRepository.save(offeredCourse);

        List<Option> options = Arrays.asList(
                Option.builder().optionText("opt 1").correct(false).build(),
                Option.builder().optionText("opt 2").correct(true).build(),
                Option.builder().optionText("opt 3").correct(false).build(),
                Option.builder().optionText("opt 4").correct(false).build());

        TestQuestion q1 = testQuestionRepository.save(TestQuestion.builder()
                .title("Question Title")
                .questionText("Question Text")
                .course(course).options(options)
                .defaultScore(2).build());

        DescriptiveQuestion q2 = descriptiveQuestionRepository.save(DescriptiveQuestion.builder()
                .title("Question Description")
                .questionText("Question Description")
                .course(course).defaultScore(2).build());

        ExamQuestion examQuestion1 = examQuestionRepository.save(ExamQuestion.builder()
                .exam(exam).question(q1).questionScore(5).build());

        ExamQuestion examQuestion2 = examQuestionRepository.save(ExamQuestion.builder()
                .exam(exam).question(q2).questionScore(5).build());


        ExamInstance examInstance = examInstanceRepository.save(ExamInstance.builder()
                .startAt(Instant.now()).endAt(Instant.now().plusSeconds(100000)).status(ExamInstanceStatus.IN_PROGRESS)
                .person(student).exam(exam).totalScore(0).build());

        TestAnswer testAnswer = testAnswerRepository.save(TestAnswer.builder()
                .examInstance(examInstance).examQuestion(examQuestion1)
                .option(options.get(0)).score(examQuestion1.getQuestionScore()).build());

        DescriptiveAnswer descriptiveAnswer = descriptiveAnswerRepository.save(DescriptiveAnswer.builder()
                .answerText("Answer Text").examInstance(examInstance).examQuestion(examQuestion2).build());


        mockMvc.perform(post("/api/student/submit/exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

    }

    @Test
    void submitAnswer() throws Exception {
        ExamTemplate exam = examRepository.save(ExamTemplate.builder()
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .offeredCourse(offeredCourse)
                .examState(ExamState.STARTED)
                .title("Exam Title").description("Exam Description").deleted(false).build());

        offeredCourse.setExamTemplates(List.of(exam));
        offeredCourseRepository.save(offeredCourse);

        List<Option> options = Arrays.asList(
                Option.builder().optionText("opt 1").correct(false).build(),
                Option.builder().optionText("opt 2").correct(true).build(),
                Option.builder().optionText("opt 3").correct(false).build(),
                Option.builder().optionText("opt 4").correct(false).build());

        TestQuestion q1 = testQuestionRepository.save(TestQuestion.builder()
                .title("Question Title")
                .questionText("Question Text")
                .course(course).options(options)
                .defaultScore(2).build());

        DescriptiveQuestion q2 = descriptiveQuestionRepository.save(DescriptiveQuestion.builder()
                .title("Question Description")
                .questionText("Question Description")
                .course(course).defaultScore(2).build());

        ExamQuestion examQuestion1 = examQuestionRepository.save(ExamQuestion.builder()
                .exam(exam).question(q1).questionScore(5).build());

        ExamInstance examInstance = examInstanceRepository.save(ExamInstance.builder()
                .startAt(Instant.now()).endAt(Instant.now().plusSeconds(100000)).status(ExamInstanceStatus.IN_PROGRESS)
                .person(student).exam(exam).totalScore(0).build());

        AnswerDTO answerDTO = AnswerDTO.builder()
                .examId(exam.getId()).optionId(options.get(0)
                        .getId()).type("test").questionId(q1.getId()).build();


        mockMvc.perform(post("/api/student/save/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private static String randomPhone() {
        StringBuilder sb = new StringBuilder("09");
        for (int i = 0; i < 9; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return sb.toString();
    }

    private static String randomNationalCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        return sb.toString();
    }
}