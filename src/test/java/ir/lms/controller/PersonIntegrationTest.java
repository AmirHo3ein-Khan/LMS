package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.model.*;
import ir.lms.model.enums.*;
import ir.lms.repository.*;
import ir.lms.util.dto.*;
import jakarta.transaction.Transactional;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PersonIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CourseRepository courseRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private OfferedCourseRepository offeredCourseRepository;


    private String accessToken;
    private OfferedCourse offeredCourse;

    @BeforeEach
    void setUp() throws Exception {
        this.accessToken = createAndLoginTestAdmin();
        Major major = majorRepository.findByMajorName("Computer").get();
        Course course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(LocalDate.now().plusDays(1),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now());
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
    }

    @Test
    void teacherRegister() throws Exception {
        PersonDTO dto = buildPersonDTO();
        mockMvc.perform(post("/api/person/teacher-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void teacherRegister_DuplicateNationalCodeAndPhoneNumber_ShouldReturn_CONFLICT() throws Exception {
        PersonDTO dto = buildPersonDTO();
        mockMvc.perform(post("/api/person/teacher-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());

        PersonDTO dto2 = buildPersonDTO();
        dto2.setNationalCode(dto.getNationalCode());
        dto2.setPhoneNumber(dto.getPhoneNumber());
        mockMvc.perform(post("/api/person/teacher-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }

    @Test
    void managerRegister() throws Exception {
        PersonDTO dto = buildPersonDTO();
        mockMvc.perform(post("/api/person/manager-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }

    @Test
    void managerRegister_DuplicateNationalCodeAndPhoneNumber_ShouldReturn_CONFLICT() throws Exception {
        PersonDTO dto = buildPersonDTO();
        mockMvc.perform(post("/api/person/manager-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());

        PersonDTO dto2 = buildPersonDTO();
        dto2.setNationalCode(dto.getNationalCode());
        dto2.setPhoneNumber(dto.getPhoneNumber());
        mockMvc.perform(post("/api/person/manager-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isConflict());
    }


    @Test
    void studentGetCourse() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
    @Test
    void studentGetCourse_InvalidOfferedCourseId_ShouldReturn_NOTFOUND() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentGetCourse_CalenderCourseRegistrationStartIsBeforeNow_ShouldReturn_FORBIDDEN() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Course course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                LocalDate.now() ,
                LocalDate.now().plusDays(1));
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentGetCourse_CalenderCourseRegistrationEndIsAfterNow_ShouldReturn_FORBIDDEN() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Course course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now() ,
                LocalDate.now().plusDays(1));
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentGetCourse_TotalUnitsPlusCourseUnitBiggerThanLimit_ShouldReturn_FORBIDDEN() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Course course = createCourse("course16", major);
        course.setUnit(6);
        courseRepository.save(course);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(LocalDate.now().plusDays(1),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now());
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        student.setOfferedCourses(List.of(createOfferedCourse(teacher , term, course),createOfferedCourse(teacher , term, course),createOfferedCourse(teacher , term, course)));
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentGetCourse_FullOfferedCourseCapacity_ShouldReturn_FORBIDDEN() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Course course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(LocalDate.now().plusDays(1),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now());
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
        offeredCourse.setCapacity(0);
        offeredCourseRepository.save(offeredCourse);
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentGetCourse_StudentMajorNotInOfferedCourseMajor_ShouldReturn_FORBIDDEN() throws Exception {
        Major major = majorRepository.findByMajorName("Computer").get();
        Major major2 = majorRepository.save(Major.builder()
                .majorName("Chemistry-" + UUID.randomUUID())
                .deleted(false)
                .build());
        Role studentRole = roleRepository.findByName("STUDENT").get();
        Course course = createCourse("course16", major);
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Person teacher = createPersonAndAccount("Teacher", "Teacher",List.of(teacherRole) , teacherRole , major);
        AcademicCalender calender = createCalender(LocalDate.now().plusDays(1),
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now());
        Term term = createTerm(major ,calender , Semester.FALL);
        offeredCourse = createOfferedCourse(teacher , term, course);
        offeredCourseRepository.save(offeredCourse);
        Person student = createPersonAndAccount("STUDENT", "STUDENT",List.of(studentRole) , studentRole , major2);
        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        mockMvc.perform(post("/api/person/take-course/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }


    @Test
    void addRoleToPerson() throws Exception {
        Person person = Person.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>())
                .build();
        personRepository.save(person);

        AddRoleRequest request = AddRoleRequest.builder()
                .role("ADMIN")
                .personId(person.getId())
                .build();

        mockMvc.perform(post("/api/person/add/person-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void addRoleToPerson_InvalidRoleName_NOTFOUND() throws Exception {
        Person person = Person.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>())
                .build();
        personRepository.save(person);

        AddRoleRequest request = AddRoleRequest.builder()
                .role("INVALID")
                .personId(person.getId())
                .build();

        mockMvc.perform(post("/api/person/add/person-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void addRoleToPerson_InvalidPersonId_NOTFOUND() throws Exception {
        AddRoleRequest request = AddRoleRequest.builder()
                .role("ADMIN")
                .personId(999L)
                .build();

        mockMvc.perform(post("/api/person/add/person-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }


    @Test
    void changeRoleTest() throws Exception {
        ChangeRoleRequestDTO dto = ChangeRoleRequestDTO.builder().role("USER").build();

        mockMvc.perform(post("/api/person/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void changeRole_InvalidRole_ShouldReturn_NOTFOUND() throws Exception {
        ChangeRoleRequestDTO dto = ChangeRoleRequestDTO.builder().role("INVALID").build();

        mockMvc.perform(post("/api/person/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeRole_RoleThatPersonDoNotHad_ShouldReturn_FORBIDDEN() throws Exception {
        ChangeRoleRequestDTO dto = ChangeRoleRequestDTO.builder().role("TEACHER").build();

        mockMvc.perform(post("/api/person/change-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRolesTest() throws Exception {
        mockMvc.perform(get("/api/person/person-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }


    @Test
    void updateProfileTest() throws Exception {
        UpdateProfileDTO build = UpdateProfileDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber("09342483948")
                .build();
        mockMvc.perform(put("/api/person/update-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(build))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void searchPersons_AsAdmin_ShouldReturnMatchingResults() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Person admin = createPersonAndAccount("Admin", "Admin", List.of(adminRole), adminRole, null);
        String token = loginAndGetToken(admin.getAccount().getUsername(), admin.getNationalCode());

        Role studentRole = roleRepository.findByName("STUDENT").orElseThrow();
        Major major = majorRepository.findByMajorName("Computer").orElseThrow();

        Person p1 = createPersonAndAccount("Alice", "Smith", List.of(studentRole), studentRole, major);
        Person p2 = createPersonAndAccount("Bob", "Johnson", List.of(studentRole), studentRole, major);
        Person p3 = createPersonAndAccount("Charlie", "Brown", List.of(studentRole), studentRole, major);

        mockMvc.perform(get("/api/person/search/{keyword}", "Alice")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.data[0].lastName").value("Smith"));
    }




    // ---------------- Helper Methods ----------------

    private String createAndLoginTestAdmin() throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").get();
        Role userRole = roleRepository.findByName("USER").get();
        Person admin = createPersonAndAccount("ADMIN", "ADMIN",List.of(adminRole , userRole) , adminRole , null);
        return loginAndGetToken(admin.getAccount().getUsername(), admin.getNationalCode());
    }

    private Person createPersonAndAccount(String firstName, String lastName, List<Role> roles , Role activeRole , Major major) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .major(major)
                .roles(new ArrayList<>(roles))
                .build();

        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .state(RegisterState.ACTIVE)
                .person(person)
                .activeRole(activeRole)
                .build();
        person.setAccount(account);
        accountRepository.save(account);

        return personRepository.save(person);
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

    private PersonDTO buildPersonDTO() {
        return PersonDTO.builder()
                .firstName("Amir Hossein")
                .lastName("Khanalipour")
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .majorName("Computer")
                .build();
    }

    private Course createCourse(String title, Major major) {
        Course course = Course.builder()
                .title(title)
                .description("Course Description")
                .major(major)
                .build();
        return courseRepository.save(course);
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

    private OfferedCourse createOfferedCourse(Person teacher , Term term, Course course) {
        OfferedCourse oc = OfferedCourse.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .teacher(teacher)
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .build();
        return offeredCourseRepository.save(oc);
    }
}
