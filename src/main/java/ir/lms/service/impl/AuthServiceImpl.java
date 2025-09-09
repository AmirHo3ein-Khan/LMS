package ir.lms.service.impl;

import ir.lms.exception.AccessNotApproveException;
import ir.lms.exception.RoleNotFoundException;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.*;
import ir.lms.service.AuthService;
import ir.lms.config.JwtService;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, AccountRepository accountRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }


    @Override
    public ApiResponseDTO studentRegister(RegisterRequestDTO registerRequestDTO) {
        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        return register(registerRequestDTO , role);
    }

    @Override
    public ApiResponseDTO teacherRegister(RegisterRequestDTO registerRequestDTO) {
        Role role = roleRepository.findByName("TEACHER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
        return register(registerRequestDTO , role);
    }


    private ApiResponseDTO register(RegisterRequestDTO request , Role role) {

        Role defualtRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        Person person = Person.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .nationalCode(request.getNationalCode())
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(defualtRole , role))
                .build();

        Account account = Account.builder()
                .username(person.getPhoneNumber())
                .password(passwordEncoder.encode(person.getNationalCode()))
                .email(request.getEmail())
                .activeRole(defualtRole)
                .state(RegisterState.WAITING)
                .build();

        person.setAccount(account);
        personRepository.save(person);
        return new ApiResponseDTO("register success.", true);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();

        final Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if (account.getState().equals(RegisterState.CONFIRM)) {


            final String token = jwtService.generateAccessToken(account.getUsername(), account.getActiveRole().getName());

            final String refreshToken = jwtService.generateRefreshToken(account.getUsername());

            return AuthResponseDTO.builder().accessToken(token).refreshToken(refreshToken).tokenType("Barrier ").build();
        }

        throw new AccessNotApproveException("Access Not Approve !");
    }




    @Override
    public ApiResponseDTO addRoleToPerson(AddRoleRequest request) {
//        Role role = roleRepository.findByName(request.role().toUpperCase())
//                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));
//        Person person = personRepository.findById(request.personId())
//                .orElseThrow(() -> new PersonNotFound("Person not found!"));
//        person.getRoles().add(role);
//        role.getPersons().add(person);
//        personRepository.save(person);
//        roleRepository.save(role);
//        return new ApiResponseDTO("Role added successfully!", true);
        return null;
    }

    @Override
    public AuthResponseDTO changeRole(ChooseRoleRequestDTO request, String username) {
//        Account account = accountRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
//
//        boolean hasRole = account.getPerson().getRoles()
//                .stream()
//                .anyMatch(r -> r.getName().equalsIgnoreCase(request.role()));
//
//        if (!hasRole) {
//            throw new PersonAccessDeniedException("Person does not have this role: " + request.role());
//        }
//        List<String> roles = account.getPerson().getRoles().stream().map(Role::getName).toList();
//
//        final String token = jwtService.generateAccessToken(account.getUsername() , roles , request.role().toUpperCase());
//        String refreshToken = jwtService.generateRefreshToken(account.getUsername());
//
//        return new AuthResponseDTO(
//                token,
//                refreshToken,
//                "Bearer",
//                List.of(request.role())
//        );
        return null;
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

//    @Override
//    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO req) {
//        final String newAccessToken = this.jwtService.refreshAccessToken(req.refreshToken());
//        final String tokenType = "Bearer";
//        return AuthenticationResponse.builder()
//                .accessToken(newAccessToken)
//                .refreshToken(req.refreshToken())
//                .tokenType(tokenType)
//                .build();
//    }


}
