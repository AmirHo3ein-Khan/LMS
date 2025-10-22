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
    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT','TEACHER','USER')";
    private static final String ADMIN_OR_MANAGER = "hasAnyRole('ADMIN','MANAGER')";



    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping("/teacher-register")
    public ResponseEntity<PersonDTO> teacherRegister(@Valid  @RequestBody PersonDTO request) {
        Person person = personService.persist(personMapper.toEntity(request));
        personService.addRoleToPerson("teacher" , person.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(personMapper.toDto(person));
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/manager-register")
    public ResponseEntity<PersonDTO> managerRegister(@Valid @RequestBody PersonDTO request) {
        Person person = personService.persist(personMapper.toEntity(request));
        personService.addRoleToPerson("manager" , person.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(personMapper.toDto(person));
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/add/person-role")
    public ResponseEntity<Void> addRoleToPerson(@Valid @RequestBody AddRoleRequest request) {
        personService.addRoleToPerson(request.getRole() , request.getPersonId());
        return ResponseEntity.ok().build();
    }


    @PreAuthorize(ALL_AUTHENTICATED)
    @PostMapping("/change-role")
    public ResponseEntity<Void> changeRole(@Valid @RequestBody ChangeRoleRequestDTO request, Principal principal) {
        personService.changeRole(principal.getName(), request.getRole());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/person-roles")
    public ResponseEntity<List<String>> getRoles(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(personService.getPersonRoles(principal).stream()
                                .map(Role::getName)
                                .toList());
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @PutMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UpdateProfileDTO dto, Principal principal) {
        personService.updateProfile(profileMapper.toEntity(dto), principal);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ADMIN)
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<PersonDTO>> search(@PathVariable String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(personService.search(keyword).stream()
                                .map(personMapper::toDto)
                                .toList());
    }

    @PreAuthorize(STUDENT)
    @PostMapping("/take-course/{courseId}")
    public ResponseEntity<Void> studentGetCourse(@PathVariable Long courseId, Principal principal) {
        studentService.studentTakeCourse(courseId, principal);
        return ResponseEntity.ok().build();
    }
}
