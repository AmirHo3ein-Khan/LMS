package ir.lms.controller;

import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.RegisterRequestDTO;
import ir.lms.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final AuthService authService;

    public ManagerController(AuthService authService) {
        this.authService = authService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/register/teacher")
    public ResponseEntity<ApiResponseDTO> registerTeacher(RegisterRequestDTO request) {
        return null;
    }
}
