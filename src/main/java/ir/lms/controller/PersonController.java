package ir.lms.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.lms.model.Role;
import ir.lms.service.AuthService;
import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.ChangeRoleRequestDTO;
import ir.lms.util.dto.ResponseOfferedCourseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class PersonController {
    private final AuthService authService;

    public PersonController(AuthService authService) {
        this.authService = authService;
    }

    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT','TEACHER','USER')";

    @PreAuthorize(ALL_AUTHENTICATED)
    @PostMapping("/change-role")
    public ResponseEntity<ApiResponseDTO> changeRole(@Valid  @RequestBody ChangeRoleRequestDTO request , Principal principal) {
        authService.changeRole(principal.getName(), request.getRole());
        return ResponseEntity.ok(new ApiResponseDTO("Change role success", true));
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/person-roles")
    public ResponseEntity<ApiResponse<List<String>>> getRoles(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<String>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(authService.getPersonRoles(principal).stream()
                                .map(Role::getName)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    // todo : update profile and change pass
}
