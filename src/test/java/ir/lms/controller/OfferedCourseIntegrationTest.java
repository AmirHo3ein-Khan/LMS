package ir.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;
import ir.lms.util.dto.OfferedCourseDTO;
import ir.lms.model.*;
import ir.lms.model.enums.CourseStatus;
import ir.lms.model.enums.RegisterState;
import ir.lms.model.enums.Semester;
import ir.lms.repository.*;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class OfferedCourseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AccountRepository accountRepository;
    @Autowired private MajorRepository majorRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private TermRepository termRepository;
    @Autowired private OfferedCourseRepository offeredCourseRepository;

    private String accessToken;

    @BeforeEach
    void beforeEach() throws Exception {
        this.accessToken = loginAsAdmin();
    }

    @Test
    void createOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course9", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());
    }
    @Test
    void createOfferedCourseWithOverlapping_ShouldReturn_AccessDine() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course9", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .dayOfWeek(DayOfWeek.FRIDAY)
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated());

        OfferedCourseDTO dto2 = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now().plusMinutes(30))
                .classEndTime(LocalTime.now().plusHours(1).plusMinutes(30))
                .capacity(20)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }


    @Test
    void createOfferedCourseTest_StartTimeAndEndTimeIsNull_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course9", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void createOfferedCourse_StartTimeIsAfterEndTime_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course9", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classEndTime(LocalTime.now())
                .classStartTime(LocalTime.now().plusHours(1))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(post("/api/offeredCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void updateOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course10", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(put("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void updateOfferedCourse_InvalidOfferedCourseId_ShouldReturn_NOTFOUND() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course10", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(put("/api/offeredCourse/" + 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOfferedCourse_TermStartDateIsBeforeNow_ShouldReturn_NOT_ACCEPTABLE() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course10", major);
        Person teacher = createTeacher(major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        term.getAcademicCalender().setCourseRegistrationStart(LocalDate.now().minusDays(1));
        termRepository.save(term);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        OfferedCourseDTO dto = OfferedCourseDTO.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .capacity(20)
                .classLocation("Tehran")
                .courseId(course.getId())
                .teacherId(teacher.getId())
                .termId(term.getId())
                .build();

        mockMvc.perform(put("/api/offeredCourse/" + offeredCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    void deleteOfferedCourseTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course11", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        mockMvc.perform(delete("/api/offeredCourse/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOfferedCourse_InvalidCourseId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(delete("/api/offeredCourse/" + 999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdTest() throws Exception {
        Major major = getMajor("Computer");
        Course course = createCourse("course12", major);
        AcademicCalender calender = createCalender(LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 10));
        Term term = createTerm(major,calender , Semester.FALL);
        OfferedCourse offeredCourse = createOfferedCourse(course, term);

        mockMvc.perform(get("/api/offeredCourse/" + offeredCourse.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findById_InvalidCourseId_ShouldReturn_NOTFOUND() throws Exception {
        mockMvc.perform(get("/api/offeredCourse/" + 999)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get("/api/offeredCourse")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findAllOfferedCoursesOfTeacherTest() throws Exception {
        Role role = roleRepository.findByName("TEACHER").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        Person teacher = createPerson("TEACHER", "TEACHER", role, major);
        Account account = createAccount(teacher, role);
        teacher.setAccount(account);
        personRepository.save(teacher);

        String token = loginAndGetToken(teacher.getPhoneNumber(), teacher.getNationalCode());

        List<OfferedCourse> offeredCourses = createCourse_Calender_Term_OfferedCourse_With_Teacher(major, teacher);

        mockMvc.perform(get("/api/offeredCourse/teacher-courses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[0].courseTitle").value(offeredCourses.get(0).getCourse().getTitle()))
                .andExpect(jsonPath("$.data[1].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[1].courseTitle").value(offeredCourses.get(1).getCourse().getTitle()));
    }

    @Test
    void findAllOfferedCoursesOfStudentTest() throws Exception {
        Role teacherRole = roleRepository.findByName("TEACHER").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        Person teacher = createPerson("TEACHER", "TEACHER", teacherRole, major);
        Account teacherAccount = createAccount(teacher, teacherRole);
        teacher.setAccount(teacherAccount);
        personRepository.save(teacher);

        List<OfferedCourse> offeredCourses = createCourse_Calender_Term_OfferedCourse_With_Teacher(major, teacher);
        Role role = roleRepository.findByName("STUDENT").get();

        Person student = createPerson("STUDENT", "STUDENT", role, major);
        Account account = createAccount(student, role);
        student.setAccount(account);
        personRepository.save(student);

        String token = loginAndGetToken(student.getPhoneNumber(), student.getNationalCode());
        student.setOfferedCourses(offeredCourses);

        mockMvc.perform(get("/api/offeredCourse/student-courses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[0].courseTitle").value(offeredCourses.get(0).getCourse().getTitle()))
                .andExpect(jsonPath("$.data[1].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[1].courseTitle").value(offeredCourses.get(1).getCourse().getTitle()));
    }


    @Test
    void findAllOfferedCoursesOfTermTest() throws Exception {
        Role role = roleRepository.findByName("TEACHER").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        Person teacher = createPerson("TEACHER", "TEACHER", role, major);
        Account account = createAccount(teacher, role);
        teacher.setAccount(account);
        personRepository.save(teacher);

        String token = loginAndGetToken(teacher.getPhoneNumber(), teacher.getNationalCode());

        Course course1 = createCourse("Course 1", major);
        Course course2 = createCourse("Course 2", major);

        AcademicCalender calender = createCalender(
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 20),
                LocalDate.of(2025, 11, 21),
                LocalDate.of(2025, 12, 31)
        );

        Term term = createTerm(major, calender, Semester.FALL);
        OfferedCourse offeredCourse1 = createOfferedCourse(course1, term);
        OfferedCourse offeredCourse2 = createOfferedCourse(course2, term);
        offeredCourse1.setTeacher(teacher);
        offeredCourse2.setTeacher(teacher);

        mockMvc.perform(get("/api/offeredCourse/term-courses/"+term.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[0].courseTitle").value(course1.getTitle()))
                .andExpect(jsonPath("$.data[1].teacherName").value(teacher.getFirstName() + " " + teacher.getLastName()))
                .andExpect(jsonPath("$.data[1].courseTitle").value(course2.getTitle()));
    }

    @Test
    void findAllOfferedCoursesOfTerm_InvalidTermId_ShouldReturn_NOTFOUND() throws Exception {
        Role role = roleRepository.findByName("TEACHER").get();
        Major major = majorRepository.findByMajorName("Computer").get();

        Person teacher = createPerson("TEACHER", "TEACHER", role, major);
        Account account = createAccount(teacher, role);
        teacher.setAccount(account);
        personRepository.save(teacher);

        String token = loginAndGetToken(teacher.getPhoneNumber(), teacher.getNationalCode());

        mockMvc.perform(get("/api/offeredCourse/term-courses/"+ 999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ---------------- Helper Methods ----------------

    private List<OfferedCourse> createCourse_Calender_Term_OfferedCourse_With_Teacher(Major major , Person teacher){
        Course course1 = createCourse("Course 1", major);
        Course course2 = createCourse("Course 2", major);

        AcademicCalender calender = createCalender(
                LocalDate.of(2025, 11, 10),
                LocalDate.of(2025, 11, 20),
                LocalDate.of(2025, 11, 21),
                LocalDate.of(2025, 12, 31)
        );

        Term term = createTerm(major, calender, Semester.FALL);
        OfferedCourse offeredCourse1 = createOfferedCourse(course1, term);
        OfferedCourse offeredCourse2 = createOfferedCourse(course2, term);
        offeredCourse1.setTeacher(teacher);
        offeredCourse2.setTeacher(teacher);
        return List.of(offeredCourse1, offeredCourse2);
    }


    private String loginAsAdmin() throws Exception {
        Role role = roleRepository.findByName("ADMIN").get();

        Person admin = createPerson("Admin", "Admin", role, null);
        Account account = createAccount(admin, role);
        admin.setAccount(account);
        accountRepository.save(account);

        return loginAndGetToken(admin.getPhoneNumber(), admin.getNationalCode());
    }

    private Person createPerson(String firstName, String lastName, Role role, Major major) {
        Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(randomPhone())
                .nationalCode(randomNationalCode())
                .roles(new ArrayList<>(List.of(role)))
                .major(major)
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
        AuthRequestDTO auth = AuthRequestDTO.builder()
                .username(username)
                .password(password)
                .build();

        String jwtToken = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(jwtToken, AuthenticationResponse.class).getAccessToken();
    }

    private Major getMajor(String name) {
        return majorRepository.findByMajorName(name).get();
    }

    private Course createCourse(String title, Major major) {
        Course course = Course.builder().title(title).description("Course Description").major(major).build();
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

    private OfferedCourse createOfferedCourse(Course course, Term term) {
        OfferedCourse offeredCourse = OfferedCourse.builder()
                .classStartTime(LocalTime.now())
                .classEndTime(LocalTime.now().plusHours(1))
                .term(term)
                .course(course)
                .courseStatus(CourseStatus.UNFILLED)
                .capacity(20)
                .classLocation("Mashhad")
                .examTemplates(new ArrayList<>())
                .build();
        return offeredCourseRepository.save(offeredCourse);
    }

    private Person createTeacher(Major major) {
        Role role = roleRepository.findByName("TEACHER").get();
        Person teacher = createPerson("Teacher", "Teacher", role, major);
        createAccount(teacher, role);
        return teacher;
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
