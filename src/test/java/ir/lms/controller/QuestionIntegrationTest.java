package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.OptionDTO;
import ir.lms.util.dto.ExamQuestionDTO;
import ir.lms.util.dto.QuestionDTO;
import ir.lms.model.*;
import ir.lms.model.enums.*;
import ir.lms.repository.*;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestionIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private OfferedCourseRepository offeredCourseRepository;
    @Autowired private TestQuestionRepository testQuestionRepository;
    @Autowired private DescriptiveQuestionRepository descriptiveQuestionRepository;
    @Autowired private ExamQuestionRepository examQuestionRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        this.accessToken = createAndLoginTeacher();
    }

    @Test
    void createTestQuestion() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course22", major);

        QuestionDTO questionDTO = QuestionDTO.builder()
                .questionType("test")
                .questionText("question text")
                .title("title")
                .courseName(course.getTitle())
                .majorName(major.getMajorName())
                .defaultScore(2d)
                .options(List.of(
                        OptionDTO.builder().optionText("option1").correct(false).build(),
                        OptionDTO.builder().optionText("option2").correct(true).build(),
                        OptionDTO.builder().optionText("option3").correct(false).build(),
                        OptionDTO.builder().optionText("option4").correct(false).build()
                ))
                .build();

        mockMvc.perform(post("/api/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }
    @Test
    void createDescriptiveQuestion() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course13", major);

        QuestionDTO questionDTO = QuestionDTO.builder()
                .questionType("Descriptive")
                .questionText("question text")
                .title("title")
                .courseName(course.getTitle())
                .majorName(major.getMajorName())
                .defaultScore(2d)
                .build();

        mockMvc.perform(post("/api/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void createQuestion_UnknownQuestionType_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        QuestionDTO questionDTO = QuestionDTO.builder()
                .questionType("unknown")
                .build();

        mockMvc.perform(post("/api/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void assignQuestionToExamWithDefaultScore() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course14", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(term, course);
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion question = createTestQuestion(course);

        ExamQuestionDTO dto = ExamQuestionDTO.builder()
                .examId(exam.getId())
                .questionId(question.getId())
                .build();

        mockMvc.perform(post("/api/question/assign-exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void assignQuestionToExamWithAssignScore() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course14", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(term, course);
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion question = createTestQuestion(course);

        ExamQuestionDTO dto = ExamQuestionDTO.builder()
                .examId(exam.getId())
                .questionId(question.getId())
                .score(5)
                .build();

        mockMvc.perform(post("/api/question/assign-exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllQuestionsOfExam() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course15", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(term, course);
        ExamTemplate exam = createExam(offeredCourse);

        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(get("/api/question/exam-questions/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllQuestionsOfExam_InvalidExamId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/question/exam-questions/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllQuestionsOfExam_ExamIsDeleted_ShouldReturn_NOTFOUND() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course15", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(term, course);
        ExamTemplate exam = createExam(offeredCourse);
        exam.setDeleted(true);
        examRepository.save(exam);

        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(get("/api/question/exam-questions/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllQuestionsOfCourse() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course15", major);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);
        course.setQuestions(Arrays.asList(q1, q2));
        courseRepository.save(course);

        mockMvc.perform(get("/api/question/course-questions/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllQuestionsOfCourse_InvalidCourseId_ShouldReturn_NOTFOUND() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course15", major);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);
        course.setQuestions(Arrays.asList(q1, q2));
        courseRepository.save(course);

        mockMvc.perform(get("/api/question/course-questions/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllQuestionsOfCourse_CourseIsDeleted_ShouldReturn_NOTFOUND() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course15", major);
        course.setDeleted(true);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);
        course.setQuestions(Arrays.asList(q1, q2));
        courseRepository.save(course);
        mockMvc.perform(get("/api/question/course-questions/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }



    // ---------------- Helper Methods ----------------



    private String createAndLoginTeacher() throws Exception {
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPerson("Teacher", "Teacher", List.of(teacherRole));
        Account account = createAccount(teacher, teacherRole);
        return loginAndGetToken(account.getUsername(), teacher.getNationalCode());
    }

    private Person createPerson(String firstName, String lastName, List<Role> roles) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>(roles))
                .build();
        return personRepository.save(person);
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
        AuthRequestDTO authRequest = AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

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

    private Major getMajor(String name) {
        return majorRepository.findByMajorName(name).get();
    }

    private Course createCourse(String title, Major major) {
        Course course = Course.builder()
                .title(title)
                .description("Course Description")
                .major(major)
                .build();
        return courseRepository.save(course);
    }

    private Term createTerm(Major major,AcademicCalender academicCalender , Semester semester) {
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
        TestQuestion question = TestQuestion.builder()
                .title("Question Title")
                .questionText("Question Text")
                .course(course)
                .options(options)
                .defaultScore(2)
                .build();
        return testQuestionRepository.save(question);
    }

    private DescriptiveQuestion createDescriptiveQuestion(Course course) {
        DescriptiveQuestion question = DescriptiveQuestion.builder()
                .title("Descriptive Question")
                .questionText("Question Text")
                .course(course)
                .defaultScore(2)
                .build();
        return descriptiveQuestionRepository.save(question);
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
