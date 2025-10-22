package ir.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.*;
import ir.lms.model.*;
import ir.lms.model.enums.*;
import ir.lms.repository.*;
import jakarta.transaction.Transactional;
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
import java.time.LocalDateTime;
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
@Transactional
class ExamIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MajorRepository majorRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TestAnswerRepository testAnswerRepository;
    @Autowired
    private ExamInstanceRepository examInstanceRepository;
    @Autowired
    private ExamQuestionRepository examQuestionRepository;
    @Autowired
    private TestQuestionRepository testQuestionRepository;
    @Autowired
    private OfferedCourseRepository offeredCourseRepository;
    @Autowired
    private DescriptiveAnswerRepository descriptiveAnswerRepository;
    @Autowired
    private DescriptiveQuestionRepository descriptiveQuestionRepository;

    private Course course;
    private Person student;
    private OfferedCourse offeredCourse;
    private String teacherAccessToken;
    private String studentAccessToken;

    @BeforeEach
    void setup() throws Exception {
        Role teacherRole = roleRepository.findByName("TEACHER").orElseThrow();
        Role studentRole = roleRepository.findByName("STUDENT").orElseThrow();
        Major major = majorRepository.findByMajorName("Computer").orElseThrow();

        Person teacher = createPersonAndAccount("Teacher", "Teacher", List.of(teacherRole), teacherRole, major);
        student = createPersonAndAccount("student", "student", List.of(studentRole), studentRole, major);

        course = createCourse("course20", major);

        teacherAccessToken = loginAndGetToken(teacher.getPhoneNumber(), teacher.getNationalCode());
        studentAccessToken = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());

        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major, calender, Semester.FALL);
        offeredCourse = createOfferedCourse(teacher, term, course);
    }

    @Test
    void saveExam() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void saveExam_ExamStartTime_And_ExamEndTime_Null_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);
        dto.setExamStartTime(null);
        dto.setExamEndTime(null);

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void saveExam_ExamStartTimeIsAfterNow_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);
        dto.setExamStartTime(Instant.now().plusSeconds(10000));

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void saveExam_ExamEndTimeIsAfterNow_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);
        dto.setExamEndTime(Instant.now().plusSeconds(10000));

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void saveExam_ExamStartTimeIsBeforeExamEndTime_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);
        dto.setExamEndTime(Instant.parse("2025-12-23T11:00:00Z"));
        dto.setExamStartTime(Instant.parse("2025-12-23T12:00:00Z"));

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void updateExam() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        ExamDTO dto = ExamDTO.builder()
                .title("Exam Title2")
                .description("Exam Description2")
                .examStartTime(exam.getExamStartTime())
                .examEndTime(exam.getExamEndTime())
                .courseId(offeredCourse.getId())
                .build();

        mockMvc.perform(put("/api/exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateExam_InvalidExamID_ShouldReturn_NOTFOUND() throws Exception {
        ExamDTO dto = ExamDTO.builder()
                .title("Exam Title2")
                .description("Exam Description2")
                .examStartTime(Instant.parse("2025-12-23T12:00:00Z"))
                .examEndTime(Instant.parse("2025-12-23T13:00:00Z"))
                .courseId(offeredCourse.getId())
                .build();

        mockMvc.perform(put("/api/exam/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void updateExam_IsAfterExamStartTime_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        exam.setExamStartTime(Instant.parse("2025-10-10T12:00:00Z"));
        exam.setExamEndTime(Instant.parse("2025-10-10T12:00:00Z"));

        ExamDTO dto = ExamDTO.builder()
                .title("Exam Title2")
                .description("Exam Description2")
                .examStartTime(Instant.parse("2025-10-10T12:00:00Z"))
                .examEndTime(Instant.parse("2025-10-10T13:00:00Z"))
                .courseId(offeredCourse.getId())
                .build();

        mockMvc.perform(put("/api/exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void deleteExam() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        mockMvc.perform(delete("/api/exam/" + exam.getId())
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteExam_InvalidExamId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(delete("/api/exam/" + 999)
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findExamById() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        mockMvc.perform(get("/api/exam/" + exam.getId())
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findExamById_InvalidExamId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/exam/" + 999)
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllExams() throws Exception {
        saveExam("Exam1", "Desc1");
        saveExam("Exam2", "Desc2");

        mockMvc.perform(get("/api/exam")
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllExamsOfACourse() throws Exception {
        saveExam("Exam1", "Desc1");
        saveExam("Exam2", "Desc2");

        mockMvc.perform(get("/api/exam/course-exams/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllExamsOfACourse_InvalidCourseId_ShouldReturn_NOTFOUND() throws Exception {
        saveExam("Exam1", "Desc1");
        saveExam("Exam2", "Desc2");

        mockMvc.perform(get("/api/exam/course-exams/" + 999)
                        .header("Authorization", "Bearer " + teacherAccessToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void studentStartExam() throws Exception {
        student.setOfferedCourses(List.of(offeredCourse));
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void studentStartExam_InvalidExamId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(post("/api/exam/start-exam/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentStartExam_AlreadyStartedTheExam_ShouldReturn_FORBIDDEN() throws Exception {
        student.setOfferedCourses(List.of(offeredCourse));
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/exam/submit-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentStartExam_StudentDoNotHadTheCourse_shouldReturn_FORBIDDEN() throws Exception {
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Major major = majorRepository.findByMajorName("Computer").get();
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major, calender, Semester.FALL);
        Person teacher = createPersonAndAccount("ali", "akbari", List.of(teacherRole), teacherRole, major);
        OfferedCourse offeredCourse1 = createOfferedCourse(teacher, term, course);
        student.setOfferedCourses(List.of(offeredCourse1));
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentStartExam_ExamStateNotStarted_ShouldReturn_FORBIDDEN() throws Exception {
        student.setOfferedCourses(List.of(offeredCourse));
        ExamTemplate exam = createExam(offeredCourse);
        exam.setExamState(ExamState.NOT_STARTED);
        examRepository.save(exam);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentStartExam_ExamStateFinished_ShouldReturn_FORBIDDEN() throws Exception {
        student.setOfferedCourses(List.of(offeredCourse));
        ExamTemplate exam = createExam(offeredCourse);
        exam.setExamState(ExamState.FINISHED);
        examRepository.save(exam);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        mockMvc.perform(post("/api/exam/start-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentSubmitExam() throws Exception {
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        ExamQuestion eq1 = examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        ExamQuestion eq2 = examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        ExamInstance instance = createExamInstance(student, exam);
        testAnswerRepository.save(TestAnswer.builder().examInstance(instance).examQuestion(eq1).option(q1.getOptions().get(0)).score(eq1.getQuestionScore()).build());
        descriptiveAnswerRepository.save(DescriptiveAnswer.builder().examInstance(instance).examQuestion(eq2).answerText("Answer Text").build());

        mockMvc.perform(post("/api/exam/submit-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void studentSubmitExam_InvalidExamId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(post("/api/exam/submit-exam/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentSubmitExam_StudentCompletedTheExam_ShouldReturn_FORBIDDEN() throws Exception {
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        ExamQuestion eq1 = examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        ExamQuestion eq2 = examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());

        ExamInstance instance = createExamInstance(student, exam);
        testAnswerRepository.save(TestAnswer.builder().examInstance(instance).examQuestion(eq1).option(q1.getOptions().get(0)).score(eq1.getQuestionScore()).build());
        descriptiveAnswerRepository.save(DescriptiveAnswer.builder().examInstance(instance).examQuestion(eq2).answerText("Answer Text").build());

        instance.setStatus(ExamInstanceStatus.COMPLETED);
        examInstanceRepository.save(instance);
        mockMvc.perform(post("/api/exam/submit-exam/" + exam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void submitAnswer() throws Exception {
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());
        createExamInstance(student, exam);

        AnswerDTO tAnswerDTO = AnswerDTO.builder()
                .examId(exam.getId())
                .questionId(q1.getId())
                .optionId(q1.getOptions().get(0).getId())
                .type("test")
                .build();

        AnswerDTO dAnswerDTO = AnswerDTO.builder()
                .examId(exam.getId())
                .questionId(q2.getId())
                .type("descriptive")
                .answerText("Answer Text")
                .build();

        mockMvc.perform(post("/api/exam/submit-answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tAnswerDTO))
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/exam/submit-answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dAnswerDTO))
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    void gradingDescriptiveQuestionOfExam() throws Exception {
        ExamTemplate exam = createExam(offeredCourse);
        TestQuestion q1 = createTestQuestion(course);
        DescriptiveQuestion q2 = createDescriptiveQuestion(course);

        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q1).questionScore(5).build());
        examQuestionRepository.save(ExamQuestion.builder().exam(exam).question(q2).questionScore(5).build());
        createExamInstance(student, exam);

        AnswerDTO tAnswerDTO = AnswerDTO.builder()
                .examId(exam.getId())
                .questionId(q1.getId())
                .optionId(q1.getOptions().get(0).getId())
                .type("test")
                .build();

        AnswerDTO dAnswerDTO = AnswerDTO.builder()
                .examId(exam.getId())
                .questionId(q2.getId())
                .type("descriptive")
                .answerText("Answer Text")
                .build();

        mockMvc.perform(post("/api/exam/submit-answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tAnswerDTO))
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/exam/submit-answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dAnswerDTO))
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());

        GradingDTO build = GradingDTO.builder().studentId(student.getId())
                .examId(exam.getId())
                .questionId(q1.getId())
                .score(2d)
                .build();

        mockMvc.perform(post("/api/exam/grading-descriptive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + studentAccessToken))
                .andExpect(status().isOk());
    }


    // ---------------- Helper Methods ----------------

    private ExamTemplate saveExam(String title, String description) {
        ExamTemplate exam = ExamTemplate.builder()
                .title(title)
                .description(description)
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .offeredCourse(offeredCourse)
                .examState(ExamState.STARTED)
                .deleted(false)
                .build();
        examRepository.save(exam);

        if (offeredCourse.getExamTemplates() == null) {
            offeredCourse.setExamTemplates(new ArrayList<>());
        }
        offeredCourse.getExamTemplates().add(exam);
        offeredCourseRepository.save(offeredCourse);

        return exam;
    }

    private ExamDTO buildExamDTO(OfferedCourse offeredCourse) {
        return ExamDTO.builder()
                .title("Exam Title")
                .description("Exam Description")
                .examStartTime(Instant.parse("2025-11-23T11:00:00Z"))
                .examEndTime(Instant.parse("2025-11-23T12:00:00Z"))
                .courseId(offeredCourse.getId())
                .build();
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

    private OfferedCourse createOfferedCourse(Person teacher, Term term, Course course) {
        OfferedCourse oc = OfferedCourse.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .teacher(teacher)
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .build();
        return offeredCourseRepository.save(oc);
    }

    private Person createPersonAndAccount(String firstName, String lastName, List<Role> roles, Role activeRole, Major major) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .major(major)
                .roles(new ArrayList<>(roles))
                .build();

        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(activeRole)
                .build();
        person.setAccount(account);
        accountRepository.save(account);

        return personRepository.save(person);
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
}
