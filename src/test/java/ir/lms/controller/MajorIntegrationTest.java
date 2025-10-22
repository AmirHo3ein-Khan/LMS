package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.MajorDTO;
import ir.lms.model.Account;
import ir.lms.model.Major;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.MajorRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class MajorIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String accessToken;


    @BeforeEach
    void setUp() throws Exception {
        Role role = roleRepository.findByName("ADMIN").orElseThrow();

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

        AuthRequestDTO loginRequest = AuthRequestDTO.builder()
                .username(admin.getPhoneNumber())
                .password(admin.getNationalCode())
                .build();

        String jwtResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        this.accessToken = objectMapper.readValue(jwtResponse, AuthenticationResponse.class).getAccessToken();
    }

    @Test
    void shouldSaveMajor() throws Exception {
        MajorDTO request = MajorDTO.builder()
                .majorName("Electronics-" + UUID.randomUUID())
                .build();

        mockMvc.perform(post("/api/major")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.majorName").value(request.getMajorName()));
    }


    @Test
    void saveMajor_DuplicateName_ShouldReturn_CONFLICT() throws Exception {
        MajorDTO request1 = MajorDTO.builder()
                .majorName("Electronics11")
                .build();
        MajorDTO request2 = MajorDTO.builder()
                .majorName("Electronics11")
                .build();

        mockMvc.perform(post("/api/major")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.majorName").value(request1.getMajorName()));
        mockMvc.perform(post("/api/major")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateMajorSuccessfully() throws Exception {
        Major existing = majorRepository.save(
                Major.builder().majorName("Computer-" + UUID.randomUUID()).build()
        );

        MajorDTO updateRequest = MajorDTO.builder()
                .majorName("Mechanic-" + UUID.randomUUID())
                .build();

        mockMvc.perform(put("/api/major/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName").value(updateRequest.getMajorName()));
    }

    @Test
    void updateMajor_InvalidMajorID_ShouldReturn_NOTFOUND() throws Exception {
        MajorDTO updateRequest = MajorDTO.builder()
                .majorName("Mechanic-" + UUID.randomUUID())
                .build();

        mockMvc.perform(put("/api/major/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMajor_WithExistedMajorName_ShouldReturn_CONFLICT() throws Exception {
        Major existing = majorRepository.save(
                Major.builder().majorName("Computer-111").build()
        );

        MajorDTO updateRequest = MajorDTO.builder()
                .majorName("Mechanic-111")
                .build();

        mockMvc.perform(put("/api/major/{id}", existing.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName").value(updateRequest.getMajorName()));

        Major newMajor = majorRepository.save(
                Major.builder().majorName("Computer-222").build()
        );

        MajorDTO updateNewMajor = MajorDTO.builder()
                .majorName("Mechanic-111")
                .build();
        mockMvc.perform(put("/api/major/{id}", newMajor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateNewMajor))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldDeleteMajor() throws Exception {
        Major toDelete = majorRepository.save(
                Major.builder().majorName("DeleteMe-" + UUID.randomUUID()).build()
        );

        mockMvc.perform(delete("/api/major/{id}", toDelete.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMajor_InvalidMajorId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(delete("/api/major/{id}", 999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindMajorById() throws Exception {
        Major saved = majorRepository.save(
                Major.builder().majorName("Physics-" + UUID.randomUUID()).deleted(false).build()
        );

        mockMvc.perform(get("/api/major/{id}", saved.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName").value(saved.getMajorName()));
    }

    @Test
    void findMajorById_InvalidMajorId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/major/{id}", 999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFindAllMajors() throws Exception {
        Major m1 = majorRepository.save(Major.builder()
                .majorName("Chemistry-" + UUID.randomUUID())
                .deleted(false)
                .build());
        Major m2 = majorRepository.save(Major.builder()
                .majorName("Biology-" + UUID.randomUUID())
                .deleted(false)
                .build());

        mockMvc.perform(get("/api/major")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].majorName", containsInAnyOrder(
                        "Computer",
                        m1.getMajorName(),
                        m2.getMajorName()
                )));
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
