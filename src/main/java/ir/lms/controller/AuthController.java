package ir.lms.controller;

import ir.lms.util.dto.*;
import ir.lms.model.Person;
import ir.lms.service.AuthService;
import ir.lms.util.dto.mapper.PersonMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final PersonMapper personMapper;

    public AuthController(AuthService authService, PersonMapper personMapper) {
        this.authService = authService;
        this.personMapper = personMapper;
    }

    @PostMapping("/student-register")
    public ResponseEntity<ApiResponse<PersonDTO>> studentRegister(@Valid @RequestBody PersonDTO request) {
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("student" , person.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PersonDTO>builder()
                        .success(true)
                        .message("Register.success")
                        .data(personMapper.toDto(person))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid  @RequestBody AuthRequestDTO request) {
        AuthResponseDTO login = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader(value = "Authorization", required = false) String authorizeRequest) {
        String token = null;
        if (authorizeRequest != null && authorizeRequest.startsWith("Bearer ")) {
            token = authorizeRequest.substring(7);
        }
        authService.logOut(token);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<String>builder()
                        .success(true)
                        .timestamp(Instant.now().toString())
                        .message("user.logout.success")
                        .build()
        );
    }



//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO req) {
//        return ResponseEntity.ok(accountService.refreshToken(req));
//    }

}
