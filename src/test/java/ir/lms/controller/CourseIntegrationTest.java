package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.CourseDTO;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class CourseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CourseRepository courseRepository;
    @Autowired private MajorRepository majorRepository;

    private String accessToken;

    @BeforeAll
    void setupAdmin() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Person admin = createPerson("Admin", "Admin", adminRole);
        Account account = createAccount(admin, adminRole);
        this.accessToken = loginAndGetToken(admin.getPhoneNumber(), admin.getNationalCode());
    }

    @Test
    void saveCourse() throws Exception {
        Major major = createMajor("Computer" + UUID.randomUUID());

        CourseDTO dto = CourseDTO.builder()
                .title("course1")
                .unit(2)
                .description("Course Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value(dto.getTitle()));
    }

    @Test
    void saveCourse_MajorDeleted_ShouldReturn_NotFound() throws Exception {
        Major major = createMajor("Computer" + UUID.randomUUID());
        major.setDeleted(true);
        majorRepository.save(major);

        CourseDTO dto = CourseDTO.builder()
                .title("course1")
                .unit(2)
                .description("Course Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveCourse_CourseExistInMajor_ShouldReturn_Conflict() throws Exception {
        Major major = createMajor("Computer" + UUID.randomUUID());
        majorRepository.save(major);

        CourseDTO dto = CourseDTO.builder()
                .title("course1")
                .unit(2)
                .description("Course Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());

        CourseDTO dto2 = CourseDTO.builder()
                .title("course1")
                .unit(2)
                .description("Course Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCourse() throws Exception {
        Major major = createMajor("Accounting");
        Course course = createCourse("Old Title", "Old Desc", major);

        CourseDTO dto = CourseDTO.builder()
                .title("New Title")
                .unit(2)
                .description("New Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(put("/api/course/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New Title"));
    }

    @Test
    void updateCourse_CourseIsDeleted_ShouldReturn_NotFound() throws Exception {
        Major major = createMajor("Accounting");
        Course course = createCourse("Old Title", "Old Desc", major);
        course.setDeleted(true);
        courseRepository.save(course);

        CourseDTO dto = CourseDTO.builder()
                .title("New Title")
                .unit(2)
                .description("New Description")
                .majorName(major.getMajorName())
                .build();

        mockMvc.perform(put("/api/course/" + course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCourse() throws Exception {
        Major major = createMajor("English");
        Course course = createCourse("To Delete", "Desc", major);

        mockMvc.perform(delete("/api/course/" + course.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Assertions.assertTrue(courseRepository.findById(course.getId()).get().isDeleted());
    }

    @Test
    void deleteCourse_InvalidCourseId_ShouldReturn_NotFound() throws Exception {
        mockMvc.perform(delete("/api/course/" + 99999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findCourseById() throws Exception {
        Major major = createMajor("IT");
        Course course = createCourse("Find Me", "Desc", major);

        mockMvc.perform(get("/api/course/" + course.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Find Me"));
    }

    @Test
    void findCourseById_InvalidCourseId_ShouldReturn_NotFound() throws Exception {
        mockMvc.perform(get("/api/course/" + 9999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllCourses() throws Exception {
        Major major = createMajor("Math");
        createCourse("C1", "Desc1", major);
        createCourse("C2", "Desc2", major);

        mockMvc.perform(get("/api/course")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(1)));
    }

    @Test
    void findAllMajorCourses() throws Exception {
        Major major = createMajor("Math");
        createCourse("C1", "Desc1", major);
        createCourse("C2", "Desc2", major);

        mockMvc.perform(get("/api/course/major-courses")
                        .param("majorName", major.getMajorName())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", greaterThan(1)));
    }

    @Test
    void findAllMajorCourses_InvalidMajorName_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/course/major-courses")
                        .param("majorName", "INVALID")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    // ---------------- Helper Methods ----------------

    private Person createPerson(String firstName, String lastName, Role role) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
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
        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readValue(response, AuthenticationResponse.class).getAccessToken();
    }

    private Major createMajor(String name) {
        return majorRepository.save(Major.builder().majorName(name).deleted(false).build());
    }

    private Course createCourse(String title, String description, Major major) {
        return courseRepository.save(Course.builder()
                .title(title)
                .unit(2)
                .description(description)
                .major(major)
                .deleted(false)
                .build());
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
