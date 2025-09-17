package ir.lms.controller;

import ir.lms.model.Role;
import ir.lms.service.AuthService;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.ChangeRoleRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class PersonController {
    private final AuthService authService;

    public PersonController(AuthService authService) {
        this.authService = authService;
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STUDENT') or hasRole('TEACHER') or hasRole('USER')")
    @PostMapping("/change/role")
    public ResponseEntity<ApiResponseDTO> changeRole(@RequestBody ChangeRoleRequestDTO request ,  Principal principal) {
        authService.changeRole(principal.getName(), request.getRole());
        return ResponseEntity.ok(new ApiResponseDTO("Change role success", true));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('STUDENT') or hasRole('TEACHER') or hasRole('USER')")
    @GetMapping("/get/roles")
    public ResponseEntity<List<String>> getRoles(Principal principal) {
        return ResponseEntity.ok(authService.getPersonRoles(principal).stream().map(Role::getName).toList());
    }
}
