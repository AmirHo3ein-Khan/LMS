package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthenticationResponse;
import ir.lms.dto.auth.ChangeRoleRequestDTO;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonIntegrationTest {
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

    private String accessToken;


    @BeforeEach
    void beforeEach() throws Exception {
        Role userRole = roleRepository.findByName("USER").get();
        Role adminRole = roleRepository.findByName("ADMIN").get();

        Person person = Person.builder().firstName("user").lastName("user").phoneNumber(randomPhone())
                .nationalCode(randomNationalCode()).roles(List.of(userRole , adminRole)).build();
        personRepository.save(person);

        Account account = Account.builder().username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE).person(person).activeRole(userRole).build();
        person.setAccount(account);
        accountRepository.save(account);

        AuthRequestDTO build = AuthRequestDTO.builder().username(person.getPhoneNumber())
                .password(person.getNationalCode()).build();

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
    void changeRole() throws Exception {
        ChangeRoleRequestDTO build = ChangeRoleRequestDTO.builder().role("ADMIN").build();

        mockMvc.perform(post("/api/user/change/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void getRoles() throws Exception {
        mockMvc.perform(get("/api/user/get/roles")
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