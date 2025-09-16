package ir.lms.controller;

import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthResponseDTO;
import ir.lms.dto.auth.PersonDTO;
import ir.lms.model.Person;
import ir.lms.dto.ApiResponseDTO;
import ir.lms.service.AuthService;
import ir.lms.dto.mapper.PersonMapper;
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
    private final PersonMapper personMapper;

    public AuthController(AuthService authService, PersonMapper personMapper) {
        this.authService = authService;
        this.personMapper = personMapper;
    }


    @PostMapping("/student/register")
    public ResponseEntity<ApiResponseDTO> studentRegister(@RequestBody PersonDTO request) {
        Person person = authService.persist(personMapper.toEntity(request));
        authService.addRoleToPerson("student" , person.getId());
        ApiResponseDTO responseDTO = new ApiResponseDTO("Register success" , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }



//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO req) {
//        return ResponseEntity.ok(accountService.refreshToken(req));
//    }

}
