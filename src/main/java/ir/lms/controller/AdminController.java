package ir.lms.controller;

import ir.lms.service.AuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/register/manager")
//    public ResponseEntity<ApiResponseDTO> registerManager(@RequestBody RegisterRequestDTO request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.registerManager(request));
//    }
}
