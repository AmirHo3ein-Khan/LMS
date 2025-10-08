package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.ExamDTO;
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
import java.util.ArrayList;
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
    private PersonRepository personRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private ExamRepository examRepository;

    private String accessToken;
    private OfferedCourse offeredCourse;

    @BeforeEach
    void setup() throws Exception {
        Role role = roleRepository.findByName("TEACHER").orElseThrow();
        Major major = majorRepository.findByMajorName("Computer").orElseThrow();

        Person teacher = createPersonWithAccount("TEACHER", role, major);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(teacher.getPhoneNumber())
                .password(teacher.getNationalCode())
                .build();

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        accessToken = objectMapper.readValue(response, AuthenticationResponse.class).getAccessToken();

        Course course = courseRepository.save(Course.builder()
                .title("course20")
                .description("Course Description")
                .major(major)
                .build());

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major)
                .build());

        offeredCourse = offeredCourseRepository.save(OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .build());
    }

    @Test
    void saveExam() throws Exception {
        ExamDTO dto = buildExamDTO(offeredCourse);

        mockMvc.perform(post("/api/exam")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
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
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteExam() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        mockMvc.perform(delete("/api/exam/" + exam.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findExamById() throws Exception {
        ExamTemplate exam = saveExam("Exam Title", "Exam Description");

        mockMvc.perform(get("/api/exam/" + exam.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllExams() throws Exception {
        saveExam("Exam1", "Desc1");
        saveExam("Exam2", "Desc2");

        mockMvc.perform(get("/api/exam")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllExamsOfACourse() throws Exception {
        saveExam("Exam1", "Desc1");
        saveExam("Exam2", "Desc2");

        mockMvc.perform(get("/api/exam/course-exams/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    // ---------------- Helper Methods ----------------

    private Person createPersonWithAccount(String name, Role role, Major major) {
        Person person = Person.builder()
                .firstName(name)
                .lastName(name)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .major(major)
                .build();
        personRepository.saveAndFlush(person);

        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(role)
                .build();
        accountRepository.saveAndFlush(account);

        person.setAccount(account);
        return person;
    }

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
}
