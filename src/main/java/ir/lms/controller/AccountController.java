package ir.lms.controller;

import ir.lms.service.PersonService;
import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.ChangePassDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;

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
    public ResponseEntity<Void> changePassword(@RequestBody ChangePassDTO dto, Principal principal) {
        personService.changePassword(dto, principal);
        return ResponseEntity.ok().build();

    }

    @PreAuthorize(ADMIN)
    @PostMapping("/active-account/{id}")
    public ResponseEntity<Void> activeAccount(@PathVariable Long id) {
        personService.activeAccount(id);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize(ADMIN)
    @PostMapping("/inactive-account/{id}")
    public ResponseEntity<Void> inactiveAccount(@PathVariable Long id) {
        personService.inactiveAccount(id);
        return ResponseEntity.ok().build();
    }

}
