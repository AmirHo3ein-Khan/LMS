package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.model.enums.Semester;
import ir.lms.repository.*;
import ir.lms.util.dto.auth.AuthRequestDTO;
import ir.lms.util.dto.auth.AuthenticationResponse;
import ir.lms.util.dto.term.TermDTO;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TermControllerTest {

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
    private TermRepository termRepository;

    @Autowired
    private MajorRepository majorRepository;

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
    void save() throws Exception {

        TermDTO build = TermDTO.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/term")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void update() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        TermDTO build = TermDTO.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .majorName("Computer").build();

        mockMvc.perform(put("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();

        Term term = termRepository.save(Term.builder()
                .startDate(LocalDate.of(2025, 11, 10))
                .endDate(LocalDate.of(2025, 11, 20))
                .semester(Semester.FALL)
                .major(major).build());

        mockMvc.perform(get("/api/term/" + term.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/term")
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