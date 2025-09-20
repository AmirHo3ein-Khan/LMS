package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.model.*;
import ir.lms.model.enums.CourseStatus;
import ir.lms.model.enums.RegisterState;
import ir.lms.model.enums.Semester;
import ir.lms.repository.*;
import ir.lms.util.dto.auth.AuthRequestDTO;
import ir.lms.util.dto.auth.AuthenticationResponse;
import ir.lms.util.dto.offeredCourse.OfferedCourseDTO;
import ir.lms.util.mapper.OfferedCourseMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OfferedCourseControllerTest {
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
    private MajorRepository majorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private OfferedCourseRepository offeredCourseRepository;

    @Autowired
    private OfferedCourseMapper mapper;

    private String accessToken;


    @BeforeEach
    void beforeEach() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();

        Person admin = Person.builder().firstName("Admin").lastName("Admin").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).roles(List.of(role)).build();
        personRepository.save(admin);

        Account account = Account.builder().username(admin.getPhoneNumber())
                .password(passwordEncoder.encode(admin.getNationalCode()))
                .state(RegisterState.ACTIVE).person(admin).activeRole(role).build();
        admin.setAccount(account);
        accountRepository.save(account);

        AuthRequestDTO build = AuthRequestDTO.builder().username(admin.getPhoneNumber())
                .password(admin.getNationalCode()).build();

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
    }

    @Test
    void createOfferedCourse() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Course course = Course.builder().title("Course Title").description("Course Description")
                .description("Course Description").major(major).build();
        courseRepository.save(course);

        Role role = roleRepository.findByName("TEACHER").get();

        Person teacher = Person.builder().firstName("teacher").lastName("teacher").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).roles(List.of(role)).build();
        personRepository.save(teacher);

        Account account = Account.builder().username(teacher.getPhoneNumber())
                .password(passwordEncoder.encode(teacher.getNationalCode()))
                .state(RegisterState.ACTIVE).person(teacher).activeRole(role).build();
        teacher.setAccount(account);
        accountRepository.save(account);


        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        OfferedCourseDTO offeredCourse = OfferedCourseDTO.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z")).capacity(20)
                .classLocation("tehran").courseId(course.getId())
                .teacherId(teacher.getId()).termId(term.getId()).build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offeredCourse))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void update() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Course course = Course.builder().title("Course Title").description("Course Description")
                .description("Course Description").major(major).build();
        courseRepository.save(course);

        Role role = roleRepository.findByName("TEACHER").get();

        Person teacher = Person.builder().firstName("teacher").lastName("teacher").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).major(major).roles(List.of(role)).build();
        personRepository.save(teacher);

        Account account = Account.builder().username(teacher.getPhoneNumber())
                .password(passwordEncoder.encode(teacher.getNationalCode()))
                .state(RegisterState.ACTIVE).person(teacher).activeRole(role).build();
        teacher.setAccount(account);
        accountRepository.save(account);

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        OfferedCourse offeredCourse = OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term).course(course).courseStatus(CourseStatus.UNFILLED)
                .capacity(20).classLocation("Mashhad").build();

        offeredCourseRepository.save(offeredCourse);

        OfferedCourseDTO offeredCourseDTO = OfferedCourseDTO.builder()
                .startTime(Instant.parse("2026-11-23T11:00:00Z"))
                .endTime(Instant.parse("2026-11-23T12:00:00Z")).capacity(20)
                .classLocation("tehran").courseId(course.getId())
                .teacherId(teacher.getId()).termId(term.getId()).build();

        mockMvc.perform(put("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offeredCourseDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Course course = Course.builder().title("Course Title").description("Course Description")
                .description("Course Description").major(major).build();
        courseRepository.save(course);

        Role role = roleRepository.findByName("TEACHER").get();

        Person teacher = Person.builder().firstName("teacher").lastName("teacher").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).major(major).roles(List.of(role)).build();
        personRepository.save(teacher);

        Account account = Account.builder().username(teacher.getPhoneNumber())
                .password(passwordEncoder.encode(teacher.getNationalCode()))
                .state(RegisterState.ACTIVE).person(teacher).activeRole(role).build();
        teacher.setAccount(account);
        accountRepository.save(account);

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        OfferedCourse offeredCourse = OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term).course(course).courseStatus(CourseStatus.UNFILLED)
                .capacity(20).classLocation("Mashhad").build();

        offeredCourseRepository.save(offeredCourse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Course course = Course.builder().title("Course Title").description("Course Description")
                .description("Course Description").major(major).build();
        courseRepository.save(course);

        Role role = roleRepository.findByName("TEACHER").get();

        Person teacher = Person.builder().firstName("teacher").lastName("teacher").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).major(major).roles(List.of(role)).build();
        personRepository.save(teacher);

        Account account = Account.builder().username(teacher.getPhoneNumber())
                .password(passwordEncoder.encode(teacher.getNationalCode()))
                .state(RegisterState.ACTIVE).person(teacher).activeRole(role).build();
        teacher.setAccount(account);
        accountRepository.save(account);

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        OfferedCourse offeredCourse = OfferedCourse.builder()
                .startTime(Instant.parse("2025-11-23T11:00:00Z"))
                .endTime(Instant.parse("2025-11-23T12:00:00Z"))
                .term(term).course(course).courseStatus(CourseStatus.UNFILLED)
                .capacity(20).classLocation("Mashhad").build();

        offeredCourseRepository.save(offeredCourse);

        mockMvc.perform(get("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
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