package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.util.dto.ChangePassDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String accessToken;
    private Person admin;

    @BeforeEach
    void setup() throws Exception {
        Role role = roleRepository.findByName("ADMIN").orElseThrow();
        this.admin = createAdminPerson(role);

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
    void changePassword() throws Exception {
        String oldPassword = admin.getNationalCode();
        String newPassword = "NewPass123";

        ChangePassDTO dto = new ChangePassDTO(newPassword, oldPassword);

        mockMvc.perform(put("/api/account/change-pass")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Account updated = accountRepository.findByUsername(admin.getPhoneNumber()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }

    @Test
    void changePassword_InCorrectOldPassword_ShouldReturn_FORBIDEN() throws Exception {
        String inCorrectOldPassword = "123123";
        String newPassword = "NewPass123";

        ChangePassDTO dto = new ChangePassDTO(newPassword, inCorrectOldPassword);

        mockMvc.perform(put("/api/account/change-pass")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void activeAccount() throws Exception {
        Account account = createAccountWithState(RegisterState.PENDING);

        mockMvc.perform(post("/api/account/active-account/" + account.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updated = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(RegisterState.ACTIVE, updated.getState());
    }

    @Test
    void activeAccount_IncorrectAccountId_ShouldReturn_NOTFOUND() throws Exception {
        long incorrectAccountId = 999L;

        mockMvc.perform(post("/api/account/active-account/" + incorrectAccountId)

                      .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void inactiveAccount() throws Exception {
        Account account = createAccountWithState(RegisterState.ACTIVE);

        mockMvc.perform(post("/api/account/inactive-account/" + account.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updated = accountRepository.findById(account.getId()).orElseThrow();
        assertEquals(RegisterState.INACTIVE, updated.getState());
    }

    @Test
    void inActiveAccount_IncorrectAccountId_ShouldReturn_NOTFOUND() throws Exception {
        long incorrectAccountId = 999L;

        mockMvc.perform(post("/api/account/inactive-account/" + incorrectAccountId)

                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
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
