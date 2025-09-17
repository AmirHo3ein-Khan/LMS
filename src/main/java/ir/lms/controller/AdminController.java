package ir.lms.controller;

import ir.lms.util.dto.auth.PersonDTO;
import ir.lms.util.mapper.PersonMapper;
import ir.lms.model.Person;
import ir.lms.service.AuthService;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.AddRoleRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {


    private final AuthService authService;
    private final PersonMapper personMapper;

    public AdminController(AuthService authService, PersonMapper personMapper) {
        this.authService = authService;
        this.personMapper = personMapper;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/teacher/register")
    public ResponseEntity<ApiResponseDTO> teacherRegister(@RequestBody PersonDTO request) {
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("teacher" , person.getId());
        ApiResponseDTO responseDTO = new ApiResponseDTO("Register success" , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/manager/register")
    public ResponseEntity<ApiResponseDTO> managerRegister(@RequestBody PersonDTO request) {
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("manager" , person.getId());
        ApiResponseDTO responseDTO = new ApiResponseDTO("Register success" , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/role")
    public ResponseEntity<ApiResponseDTO> addRoleToPerson(@RequestBody AddRoleRequest request) {
        authService.addRoleToPerson(request.getRole() , request.getPersonId());
        return ResponseEntity.ok(new ApiResponseDTO("Add role success", true));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/active/{id}")
    public ResponseEntity<ApiResponseDTO> activeAccount(@PathVariable Long id) {
        authService.activeAccount(id);
        String msg = "user active successfully.";
        return ResponseEntity.ok(new ApiResponseDTO(msg , true));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/inactive/{id}")
    public ResponseEntity<ApiResponseDTO> inactiveAccount(@PathVariable Long id) {
        authService.inactiveAccount(id);
        String msg = "user inactive successfully.";
        return ResponseEntity.ok(new ApiResponseDTO(msg , true));
    }

}
