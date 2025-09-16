package ir.lms.service.impl;

import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthResponseDTO;
import ir.lms.dto.auth.ChangeRoleRequestDTO;
import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.DuplicateException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.*;
import ir.lms.service.AuthService;
import ir.lms.config.JwtService;
import ir.lms.service.base.BaseServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;


@Service
public class AuthServiceImpl extends BaseServiceImpl<Person, Long> implements AuthService {

    private final static String NOT_FOUND = "%s not found!";
    private final static String EXIST = "Already exists!";

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    protected AuthServiceImpl(JpaRepository<Person, Long> repository, AuthenticationManager authenticationManager,
                              AccountRepository accountRepository, RoleRepository roleRepository,
                              JwtService jwtService, PasswordEncoder passwordEncoder,
                              PersonRepository personRepository) {
        super(repository);
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
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
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));

        if (account.getState().equals(RegisterState.ACTIVE)) {

            account.setAuthId(UUID.randomUUID());
            accountRepository.save(account);

            final String token = jwtService.generateAccessToken(account.getUsername(), account.getActiveRole().getName());

            final String refreshToken = jwtService.generateRefreshToken(account.getUsername());

            return AuthResponseDTO.builder().accessToken(token)
                    .refreshToken(refreshToken).tokenType("Barrier ")
                    .activeRole(account.getActiveRole().getName()).build();
        }

        throw new AccessDeniedException("You don't have access. Your Account not active!");
    }

    @Override
    public void changeRole(String username , String roleName) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));

        Role role = roleRepository.findByName(roleName.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Role")));

        if (account.getPerson().getRoles().contains(role)) {
            account.setActiveRole(role);
            accountRepository.save(account);
        }
        else throw new AccessDeniedException("Don't have permission to change to this role!");
    }

    @Override
    public void addRoleToPerson(String role, Long personId) {
        Role founded = roleRepository.findByName(role.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Role")));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Person")));
        person.getRoles().add(founded);
        personRepository.save(person);
    }

    @Override
    public void activeAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format(NOT_FOUND, "Account")));
        account.setState(RegisterState.ACTIVE);
        accountRepository.save(account);
    }

    @Override
    public void inactiveAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Account")));
        account.setState(RegisterState.INACTIVE);
        accountRepository.save(account);
    }

    @Override
    public List<Role> getPersonRoles(Principal principal) {
        String username = principal.getName();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Username")));
        return account.getPerson().getRoles();
    }


    @Override
    protected void prePersist(Person person) {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Role")));
        if (personRepository.existsByNationalCode(person.getNationalCode())
                || personRepository.existsByPhoneNumber(person.getPhoneNumber())) {
            throw new DuplicateException(EXIST);
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        person.setRoles(roles);
    }

    @Override
    protected void postPersist(Person person) {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Role")));

        Account account = Account.builder()
                .username(person.getNationalCode())
                .password(passwordEncoder.encode(person.getPhoneNumber()))
                .state(RegisterState.PENDING)
                .person(person)
                .activeRole(role)
                .build();

        person.setAccount(account);
        accountRepository.save(account);
    }

}
