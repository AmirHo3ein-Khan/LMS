package ir.lms.controller;

import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.AddRoleRequest;
import ir.lms.util.dto.auth.AuthRequestDTO;
import ir.lms.util.dto.auth.AuthResponseDTO;
import ir.lms.service.AuthService;
import ir.lms.util.dto.auth.RegisterRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/student/register")
    public ResponseEntity<ApiResponseDTO> studentRegister(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.studentRegister(request));
    }


    @PostMapping("/teacher/register")
    public ResponseEntity<ApiResponseDTO> teacherRegister(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.teacherRegister(request));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }

    @PostMapping("/add/role")
    public ResponseEntity<ApiResponseDTO> addRoleToPerson(@RequestBody AddRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.addRoleToPerson(request));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO req) {
//        return ResponseEntity.ok(accountService.refreshToken(req));
//    }

}
