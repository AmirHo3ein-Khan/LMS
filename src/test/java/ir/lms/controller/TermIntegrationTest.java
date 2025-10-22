package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.SemesterUtil;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.TermDTO;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.model.enums.Semester;
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

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TermIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").get();
        Person admin = createPerson("Admin", "Admin", adminRole);
        createAccount(admin, adminRole);
        this.accessToken = loginAndGetToken(admin.getPhoneNumber(), admin.getNationalCode());
    }

    @Test
    void saveTerm() throws Exception {
        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.of(2026, 11, 20))
                .courseRegistrationEnd(LocalDate.of(2026, 11, 20))
                .classesEndDate(LocalDate.of(2026, 11, 20))
                .classesStartDate(LocalDate.of(2026, 11, 20))
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void saveTerm_TermYearLessThanCurrentYear_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.of(2024, 11, 20))
                .courseRegistrationEnd(LocalDate.of(2025, 11, 20))
                .classesEndDate(LocalDate.of(2025, 11, 20))
                .classesStartDate(LocalDate.of(2025, 11, 20))
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void saveTerm_ExistInSemesterAndMajorAndInCurrentYear_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        Major major = getMajor("Computer");

        AcademicCalender calender = createCalender(LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(10));

        Term term = createTerm(major,calender , SemesterUtil.currentSemester());

        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.now())
                .courseRegistrationEnd(LocalDate.now().plusDays(5))
                .classesStartDate(LocalDate.now().plusDays(6))
                .classesEndDate(LocalDate.now().plusDays(10))
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void updateTerm() throws Exception {
        Major major = getMajor("Computer");

        AcademicCalender calender = createCalender(LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(10));

        Term term = createTerm(major,calender , SemesterUtil.currentSemester());

        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.now())
                .courseRegistrationEnd(LocalDate.now().plusDays(5))
                .classesStartDate(LocalDate.now().plusDays(6))
                .classesEndDate(LocalDate.now().plusDays(10))
                .majorName("Computer")
                .build();

        mockMvc.perform(put("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateTerm_InvalidTermId_ShouldReturn_NOTFOUND() throws Exception {
        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.now())
                .courseRegistrationEnd(LocalDate.now().plusDays(5))
                .classesStartDate(LocalDate.now().plusDays(6))
                .classesEndDate(LocalDate.now().plusDays(10))
                .majorName("Computer")
                .build();

        mockMvc.perform(put("/api/term/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTerm_IsDeleted_ShouldReturn_NOTFOUND() throws Exception {
        Major major = getMajor("Computer");

        AcademicCalender calender = createCalender(LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(10));

        Term term = createTerm(major,calender , SemesterUtil.currentSemester());
        term.setDeleted(true);
        termRepository.save(term);

        TermDTO termDTO = TermDTO.builder()
                .courseRegistrationStart(LocalDate.now())
                .courseRegistrationEnd(LocalDate.now().plusDays(5))
                .classesStartDate(LocalDate.now().plusDays(6))
                .classesEndDate(LocalDate.now().plusDays(10))
                .majorName("Computer")
                .build();

        mockMvc.perform(put("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTerm() throws Exception {
        Major major = getMajor("Computer");
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        mockMvc.perform(delete("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTerm_InvalidTermId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(delete("/api/term/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findTermById() throws Exception {
        Major major = getMajor("Computer");
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        mockMvc.perform(get("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findTermById_InvalidTermId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/term/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllTerms() throws Exception {
        mockMvc.perform(get("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findTermCalender_ShouldReturnAcademicCalender() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").orElseGet(() ->
                majorRepository.save(Major.builder().majorName("Computer").deleted(false).build())
        );
        AcademicCalender calender = AcademicCalender.builder()
                .courseRegistrationStart(LocalDate.now().plusDays(1))
                .courseRegistrationEnd(LocalDate.now().plusDays(10))
                .classesStartDate(LocalDate.now().plusDays(11))
                .classesEndDate(LocalDate.now().plusDays(50))
                .build();
        Term term = Term.builder()
                .year(LocalDate.now().getYear()) // dynamic year
                .semester(Semester.FALL)
                .major(major)
                .academicCalender(calender)
                .build();

        term = termRepository.save(term);

        Role userRole = roleRepository.findByName("STUDENT").orElseThrow();
        Person user = createPerson("John", "Doe", userRole);
        createAccount(user, userRole);

        String token = loginAndGetToken(user.getPhoneNumber(), user.getNationalCode());

        mockMvc.perform(get("/api/term/academic-calender/{termId}", term.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("calender.get.success"))
                .andExpect(jsonPath("$.data.courseRegistrationStart").exists())
                .andExpect(jsonPath("$.data.courseRegistrationEnd").exists())
                .andExpect(jsonPath("$.data.classesStartDate").exists())
                .andExpect(jsonPath("$.data.classesEndDate").exists());
    }

    @Test
    void findTermCalender_InvalidTermId_ShouldReturn_NOTFOUDN() throws Exception {
        mockMvc.perform(get("/api/term/academic-calender/{termId}", 999)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
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

    private Major getMajor(String name) {
        return majorRepository.findByMajorName(name).get();
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
