package ir.lms.controller;

import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.service.GradingService;
import ir.lms.service.PersonService;
import ir.lms.service.StudentService;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.PersonMapper;
import ir.lms.util.dto.mapper.ProfileMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;
    private final ProfileMapper profileMapper;
    private final PersonMapper personMapper;
    private final StudentService studentService;


    public PersonController(PersonService personService, ProfileMapper profileMapper,
                            PersonMapper personMapper, StudentService studentService) {
        this.personService = personService;
        this.profileMapper = profileMapper;
        this.personMapper = personMapper;
        this.studentService = studentService;
    }

    private static final String ADMIN = "hasRole('ADMIN')";
    private static final String STUDENT = "hasRole('STUDENT')";
    private static final String TEACHER = "hasRole('TEACHER')";
    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT','TEACHER','USER')";
    private static final String ADMIN_OR_MANAGER = "hasAnyRole('ADMIN','MANAGER')";



    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping("/teacher-register")
    public ResponseEntity<ApiResponse<PersonDTO>> teacherRegister(@Valid  @RequestBody PersonDTO request) {
        Person person = personService.persist(personMapper.toEntity(request));
        personService.addRoleToPerson("teacher" , person.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PersonDTO>builder()
                        .success(true)
                        .message("Register.success")
                        .data(personMapper.toDto(person))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/manager-register")
    public ResponseEntity<ApiResponse<PersonDTO>> managerRegister(@Valid @RequestBody PersonDTO request) {
        Person person = personService.persist(personMapper.toEntity(request));
        personService.addRoleToPerson("manager" , person.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PersonDTO>builder()
                        .success(true)
                        .message("Register.success")
                        .data(personMapper.toDto(person))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/add/person-role")
    public ResponseEntity<ApiResponseDTO> addRoleToPerson(@Valid @RequestBody AddRoleRequest request) {
        personService.addRoleToPerson(request.getRole() , request.getPersonId());
        return ResponseEntity.ok(new ApiResponseDTO("Add role success", true));
    }


    @PreAuthorize(ALL_AUTHENTICATED)
    @PostMapping("/change-role")
    public ResponseEntity<ApiResponseDTO> changeRole(@Valid @RequestBody ChangeRoleRequestDTO request, Principal principal) {
        personService.changeRole(principal.getName(), request.getRole());
        return ResponseEntity.ok(new ApiResponseDTO("Change role success", true));
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/person-roles")
    public ResponseEntity<ApiResponse<List<String>>> getRoles(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("roles.get.success")
                        .data(personService.getPersonRoles(principal).stream()
                                .map(Role::getName)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponseDTO> updateProfile(@RequestBody UpdateProfileDTO dto, Principal principal) {
        personService.updateProfile(profileMapper.toEntity(dto), principal);
        return ResponseEntity.ok(new ApiResponseDTO("Update profile success", true));
    }

    @PreAuthorize(ADMIN)
    @GetMapping("/search/{keyword}")
    public ResponseEntity<ApiResponse<List<PersonDTO>>> search(@PathVariable String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<PersonDTO>>builder()
                        .success(true)
                        .message("roles.get.success")
                        .data(personService.search(keyword).stream()
                                .map(personMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(STUDENT)
    @PostMapping("/take-course/{courseId}")
    public ResponseEntity<ApiResponseDTO> studentGetCourse(@PathVariable Long courseId, Principal principal) {
        studentService.studentTakeCourse(courseId, principal);
        return ResponseEntity.ok(new ApiResponseDTO("Course get success.", true));
    }
}
