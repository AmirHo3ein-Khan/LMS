package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.dto.auth.AddRoleRequest;
import ir.lms.dto.auth.RegisterDTO;
import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthenticationResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminIntegrationTest {

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

    private String accessToken;

    @BeforeEach
    void setup() throws Exception {
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        Person admin = createAdminPerson(role);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getPhoneNumber())
                .password(admin.getNationalCode())
                .build();

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        this.accessToken = objectMapper.readValue(response, AuthenticationResponse.class).getAccessToken();
    }

    @Test
    void teacherRegister() throws Exception {
        RegisterDTO dto = buildRegisterDTO();
        mockMvc.perform(post("/api/admin/teacher/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void managerRegister() throws Exception {
        RegisterDTO dto = buildRegisterDTO();
        mockMvc.perform(post("/api/admin/manager/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addRoleToPerson() throws Exception {
        Person person = Person.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .build();
        personRepository.save(person);

        AddRoleRequest request = AddRoleRequest.builder()
                .role("ADMIN")
                .personId(person.getId())
                .build();

        mockMvc.perform(post("/api/admin/add/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void activeAccount() throws Exception {
        Account account = createAccountWithState(RegisterState.PENDING);

        mockMvc.perform(post("/api/admin/active/" + account.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updated = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(RegisterState.ACTIVE, updated.getState());
    }

    @Test
    void inactiveAccount() throws Exception {
        Account account = createAccountWithState(RegisterState.ACTIVE);

        mockMvc.perform(post("/api/admin/inactive/" + account.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updated = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(RegisterState.INACTIVE, updated.getState());
    }

    // ---------------- Helper Methods ----------------

    private Person createAdminPerson(Role role) {
        Person admin = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .build();
        personRepository.saveAndFlush(admin);

        Account account = Account.builder()
                .username(admin.getPhoneNumber())
                .password(passwordEncoder.encode(admin.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(admin)
                .activeRole(role)
                .build();
        accountRepository.saveAndFlush(account);

        admin.setAccount(account);
        return admin;
    }

    private Account createAccountWithState(RegisterState state) {
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        Person person = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .build();
        personRepository.saveAndFlush(person);

        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(state)
                .person(person)
                .activeRole(role)
                .build();
        accountRepository.saveAndFlush(account);

        person.setAccount(account);
        return account;
    }

    private RegisterDTO buildRegisterDTO() {
        return RegisterDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
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
