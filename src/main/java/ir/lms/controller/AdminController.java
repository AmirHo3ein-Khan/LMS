package ir.lms.controller;

import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.PersonDTO;
import ir.lms.model.Person;
import ir.lms.service.AuthService;
import ir.lms.util.dto.AddRoleRequest;
import ir.lms.util.dto.mapper.PersonMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final AuthService authService;
    private final PersonMapper personMapper;

    public AdminController(AuthService authService, PersonMapper personMapper) {
        this.authService = authService;
        this.personMapper = personMapper;
    }

    private static final String ADMIN_OR_MANAGER = "hasRole('ADMIN') OR hasRole('MANAGER')";
    private static final String ADMIN = "hasRole('ADMIN')";

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping("/teacher-register")
    public ResponseEntity<ApiResponse<PersonDTO>> teacherRegister(@Valid  @RequestBody PersonDTO request) {
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("teacher" , person.getId());
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
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("manager" , person.getId());
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
        authService.addRoleToPerson(request.getRole() , request.getPersonId());
        return ResponseEntity.ok(new ApiResponseDTO("Add role success", true));
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/active-role/{id}")
    public ResponseEntity<ApiResponseDTO> activeAccount(@PathVariable Long id) {
        authService.activeAccount(id);
        return ResponseEntity.ok(new ApiResponseDTO("user active successfully." , true));
    }


    @PreAuthorize(ADMIN)
    @PostMapping("/inactive-role/{id}")
    public ResponseEntity<ApiResponseDTO> inactiveAccount(@PathVariable Long id) {
        authService.inactiveAccount(id);
        return ResponseEntity.ok(new ApiResponseDTO("user inactive successfully." , true));
    }

}
