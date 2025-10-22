package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.service.AuthService;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.PersonDTO;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
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

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthService authService;

    @Test
    void studentRegister() throws Exception {
        PersonDTO dto = PersonDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/auth/student-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void studentRegister_DuplicateNationalCodeAndPhoneNumber_ShouldReturn_CONFLICT() throws Exception {
        PersonDTO dto = PersonDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/auth/student-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        PersonDTO dto2 = PersonDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(dto.getPhoneNumber())
                .nationalCode(dto.getNationalCode())
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/auth/student-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isConflict());
    }

    @Test
    void login() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        Person admin = createAdminPerson(adminRole);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getNationalCode())
                .password(admin.getPhoneNumber())
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tokenType").isNotEmpty());
    }


    @Test
    void login_IncorrectUsername_ShouldReturn_Forbidden() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        Person admin = createAdminPerson(adminRole);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username("IncorrectUsername")
                .password(admin.getPhoneNumber())
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }


    @Test
    void login_IncorrectPassword_ShouldReturn_Forbidden() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        Person admin = createAdminPerson(adminRole);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getNationalCode())
                .password("IncorrectUsername")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_InactiveAccount_ShouldReturn_Forbidden() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        Person admin = createAdminPerson(adminRole);
        admin.getAccount().setState(RegisterState.INACTIVE);
        accountRepository.save(admin.getAccount());

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getNationalCode())
                .password(admin.getPhoneNumber())
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_ShouldReturn_OK_WhenValidTokenProvided() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Person admin = createAdminPerson(adminRole);

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getNationalCode())
                .password(admin.getPhoneNumber())
                .build();

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("accessToken").asText();

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("user.logout.success"));
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "bearer " + "token"))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    void logout_WithoutToken_ShouldFail() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().is4xxClientError());
    }


    private Person createAdminPerson(Role role) {
        Person admin = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .nationalCode(randomNationalCode())
                .phoneNumber(randomPhone())
                .roles(List.of(role))
                .build();

        personRepository.save(admin);

        Account account = Account.builder()
                .username(admin.getNationalCode())
                .password(passwordEncoder.encode(admin.getPhoneNumber()))
                .state(RegisterState.ACTIVE)
                .person(admin)
                .activeRole(role)
                .build();

        admin.setAccount(account);
        accountRepository.save(account);

        return admin;
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
