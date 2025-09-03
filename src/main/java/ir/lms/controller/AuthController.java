package ir.lms.controller;

import ir.lms.model.dto.*;
import ir.lms.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }


    /**
     * Register new user account
     *
     * @param request user registration details
     * @return {@link ResponseEntity} containing success message and status {@code 201 CREATED}
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.registerAccount(request));
    }

    /**
     * Authentication user and returns jwt access and refresh token.
     *
     * @param request login credential (username , password)
     * @return {@link AuthResponseDTO} containing access and refresh tokens with staus {@code 200 OK}
     */

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.login(request));
    }

    @PostMapping("/addRole")
    public ResponseEntity<ApiResponseDto> addRoleToPerson(@RequestBody AddRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.addRoleToPerson(request));
    }

    /**
     * Refresh the access token using a valid refresh token.
     * @param req DTO containing refresh token.
     * @return {@link AuthResponseDTO} containing access and refresh token with status {@code 200 OK}
     */

//    @PostMapping("/refresh")
//    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO req) {
//        return ResponseEntity.ok(userService.refreshToken(req));
//    }

}
