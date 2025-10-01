package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthenticationResponse;
import ir.lms.dto.term.TermDTO;
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
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTerm() throws Exception {
        Major major = getMajor("Computer");
        Term term = createTerm(major, LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 20));

        TermDTO termDTO = TermDTO.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .majorName("Computer")
                .build();

        mockMvc.perform(put("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(termDTO))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTerm() throws Exception {
        Major major = getMajor("Computer");
        Term term = createTerm(major, LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 20));

        mockMvc.perform(delete("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findTermById() throws Exception {
        Major major = getMajor("Computer");
        Term term = createTerm(major, LocalDate.of(2025, 11, 10), LocalDate.of(2025, 11, 20));

        mockMvc.perform(get("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllTerms() throws Exception {
        mockMvc.perform(get("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
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

    private Term createTerm(Major major, LocalDate start, LocalDate end) {
        Term term = Term.builder()
                .startDate(start)
                .endDate(end)
                .semester(Semester.FALL)
                .major(major)
                .build();
        return termRepository.save(term);
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
