package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.dto.auth.AddRoleRequest;
import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthenticationResponse;
import ir.lms.dto.auth.RegisterDTO;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    private String accessToken;


    @BeforeEach
    void beforeEach() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();

        Person admin = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .build();

        personRepository.save(admin);
        Account account = Account.builder()
                .username(admin.getPhoneNumber())
                .password(passwordEncoder.encode(admin.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(admin)
                .activeRole(role)
                .build();

        admin.setAccount(account);
        accountRepository.save(account);

        AuthRequestDTO build = AuthRequestDTO.builder().username(admin.getPhoneNumber()).password(admin.getNationalCode()).build();

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
    void teacherRegister() throws Exception {
        RegisterDTO build = RegisterDTO.builder()
                .firstName("Amir hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/admin/teacher/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void managerRegister() throws Exception {
        RegisterDTO build = RegisterDTO.builder()
                .firstName("Amir hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
                .build();

        mockMvc.perform(post("/api/admin/manager/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void addRoleToPerson() throws Exception {
        Person person = Person.builder()
                .firstName("Amir hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .build();

        personRepository.save(person);

        AddRoleRequest build = AddRoleRequest.builder().role("ADMIN").personId(person.getId()).build();

        mockMvc.perform(post("/api/admin/add/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void activeAccount() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();
        Person person = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .build();

        personRepository.save(person);
        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.PENDING)
                .person(person)
                .activeRole(role)
                .build();

        person.setAccount(account);
        accountRepository.save(account);

        mockMvc.perform(post("/api/admin/active/" + account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updatedAccount = accountRepository.findById(account.getId()).get();
        assertEquals(RegisterState.ACTIVE, updatedAccount.getState());
    }

    @Test
    void inactiveAccount() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();
        Person person = Person.builder()
                .firstName("Admin")
                .lastName("Admin")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(List.of(role))
                .build();

        personRepository.save(person);
        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(role)
                .build();

        person.setAccount(account);
        accountRepository.save(account);

        mockMvc.perform(post("/api/admin/inactive/" + account.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        Account updatedAccount = accountRepository.findById(account.getId()).get();
        assertEquals(RegisterState.INACTIVE, updatedAccount.getState());
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