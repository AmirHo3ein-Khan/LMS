package ir.lms.controller;

import ir.lms.service.PersonService;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.ChangePassDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final PersonService personService;

    public AccountController(PersonService personService) {
        this.personService = personService;
    }

    private static final String ADMIN = "hasRole('ADMIN')";
    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT','TEACHER','USER')";

    @PreAuthorize(ALL_AUTHENTICATED)
    @PutMapping("/change-pass")
    public ResponseEntity<ApiResponseDTO> changePassword(@RequestBody ChangePassDTO dto, Principal principal) {
        personService.changePassword(dto, principal);
        return ResponseEntity.ok(new ApiResponseDTO("Change password success", true));
    }

    @PreAuthorize(ADMIN)
    @PostMapping("/active-account/{id}")
    public ResponseEntity<ApiResponseDTO> activeAccount(@PathVariable Long id) {
        personService.activeAccount(id);
        return ResponseEntity.ok(new ApiResponseDTO("user active successfully." , true));
    }


    @PreAuthorize(ADMIN)
    @PostMapping("/inactive-account/{id}")
    public ResponseEntity<ApiResponseDTO> inactiveAccount(@PathVariable Long id) {
        personService.inactiveAccount(id);
        return ResponseEntity.ok(new ApiResponseDTO("user inactive successfully." , true));
    }

}
