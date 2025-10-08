package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.OfferedCourseDTO;
import ir.lms.model.*;
import ir.lms.model.enums.CourseStatus;
import ir.lms.model.enums.RegisterState;
import ir.lms.model.enums.Semester;
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
class OfferedCourseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountRepository accountRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private OfferedCourseRepository offeredCourseRepository;

    private String accessToken;

    @BeforeEach
    void beforeEach() throws Exception {
        this.accessToken = loginAsAdmin();
    }

    @Test
    void createOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course9", major);
        Person teacher = createTeacher(major);
        Term term = createTerm(major, LocalDate.of(2025,11,10), LocalDate.of(2025,11,20), Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void updateOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course10", major);
        Person teacher = createTeacher(major);
        Term term = createTerm(major, LocalDate.of(2025,11,10), LocalDate.of(2025,11,20), Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .startTime(Instant.parse("2026-11-23T11:00:00Z"))
                .endTime(Instant.parse("2026-11-23T12:00:00Z"))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(put("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course11", major);
        Term term = createTerm(major, LocalDate.of(2025,11,10), LocalDate.of(2025,11,20), Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        mockMvc.perform(delete("/api/offeredCourse/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course12", major);
        Term term = createTerm(major, LocalDate.of(2025,11,10), LocalDate.of(2025,11,20), Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        mockMvc.perform(get("/api/offeredCourse/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get("/api/offeredCourse")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    // ---------------- Helper Methods ----------------

    private String loginAsAdmin() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();

        Person admin = createPerson("Admin", "Admin", role, null);
        Account account = createAccount(admin, role);
        admin.setAccount(account);
        accountRepository.save(account);

        return loginAndGetToken(admin.getPhoneNumber(), admin.getNationalCode());
    }

    private Person createPerson(String firstName, String lastName, Role role, Major major) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>(List.of(role)))
                .major(major)
                .build();
        return personRepository.save(person);
    }

    private Account createAccount(Person person, Role role) {
        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(role)
                .build();
        person.setAccount(account);
        return accountRepository.save(account);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        AuthRequestDTO auth = AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        String jwtToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(jwtToken, AuthenticationResponse.class).getAccessToken();
    }

    private Major getMajor(String name) {
        return majorRepository.findByMajorName(name).get();
    }

    private Course createCourse(String title, Major major) {
        Course course = Course.builder().title(title).description("Course Description").major(major).build();
        return courseRepository.save(course);
    }

    private Term createTerm(Major major, LocalDate start, LocalDate end, Semester semester) {
        Term term = Term.builder().startDate(start).endDate(end).semester(semester).major(major).build();
        return termRepository.save(term);
    }

    private OfferedCourse createOfferedCourse(Course course, Term term) {
        OfferedCourse offeredCourse = OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .examTemplates(new ArrayList<>())
                .build();
        return offeredCourseRepository.save(offeredCourse);
    }

    private Person createTeacher(Major major) {
        Role role = roleRepository.findByName("TEACHER").get();
        Person teacher = createPerson("Teacher", "Teacher", role, major);
        createAccount(teacher, role);
        return teacher;
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
