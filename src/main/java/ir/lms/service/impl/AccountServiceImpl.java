package ir.lms.service.impl;

import ir.lms.exception.AccessNotApproveException;
import ir.lms.exception.PersonNotFound;
import ir.lms.exception.RoleNotFoundException;
import ir.lms.model.*;
import ir.lms.model.dto.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.*;
import ir.lms.service.AccountService;
import ir.lms.service.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final GroupMangerRepository groupMangerRepository;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    public AccountServiceImpl(GroupMangerRepository groupMangerRepository, AuthenticationManager authenticationManager, AccountRepository accountRepository,
                              StudentRepository studentRepository, TeacherRepository teacherRepository, PasswordEncoder passwordEncoder,
                              RoleRepository roleRepository, JwtService jwtService, PersonRepository personRepository) {
        this.groupMangerRepository = groupMangerRepository;
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.personRepository = personRepository;
    }

    @Override
    public ApiResponseDto registerAccount(RegisterRequestDTO registerRequestDTO) {
        Role role = roleRepository.findByName(registerRequestDTO.role())
                .orElseThrow(() -> new RoleNotFoundException("Invalid role!"));
        if (role.getName().equalsIgnoreCase("STUDENT")) {
            studentRegister(registerRequestDTO, role);
        } else if (role.getName().equalsIgnoreCase("TEACHER")) {
            teacherRegister(registerRequestDTO, role);
        } else if (role.getName().equalsIgnoreCase("MANAGER")) {
            groupManagerRegister(registerRequestDTO, role);
        } else throw new RoleNotFoundException("Role not found!");

        return new ApiResponseDto("Register success", true);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO request) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();

        final Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        if (account.getPerson().getRoles().contains(roleRepository.findByName("ADMIN").get()) ||
        account.getState().equals(RegisterState.CONFIRM)) {

            final String token = jwtService.generateAccessToken(account.getUsername());
            final String refreshToken = jwtService.generateRefreshToken(account.getUsername());
            if (account.getPerson().getRoles().size() > 1) {

            }
            return new AuthResponseDTO(token, refreshToken, "Bearer");
        }
        else throw new AccessNotApproveException("Access Not Approve");
    }

    @Override
    public ApiResponseDto addRoleToPerson(AddRoleRequest request) {
        Role role = roleRepository.findByName(request.role().toUpperCase())
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));
        Person person = personRepository.findById(request.personId())
                .orElseThrow(() -> new PersonNotFound("Person not found!"));
        person.getRoles().add(role);
        role.getPersons().add(person);
        personRepository.save(person);
        roleRepository.save(role);
        return new ApiResponseDto("Role added successfully!", true);
    }

    private void studentRegister(RegisterRequestDTO registerRequestDTO, Role role) {
        Student student = Student.builder()
                .firstName(registerRequestDTO.firstName())
                .lastName(registerRequestDTO.lastName())
                .nationalCode(registerRequestDTO.nationalCode())
                .roles(Collections.singletonList(role))
                .build();

        studentRepository.save(student);

        Account account = Account.builder()
                .email(registerRequestDTO.email())
                .username(registerRequestDTO.username())
                .password(passwordEncoder.encode(registerRequestDTO.password()))
                .state(RegisterState.WAITING)
                .person(student)
                .build();
        student.setAccount(account);

        accountRepository.save(account);
        studentRepository.save(student);
    }

    private void teacherRegister(RegisterRequestDTO registerRequestDTO, Role role) {
        Teacher teacher = Teacher.builder()
                .firstName(registerRequestDTO.firstName())
                .lastName(registerRequestDTO.lastName())
                .nationalCode(registerRequestDTO.nationalCode())
                .roles(Collections.singletonList(role))
                .build();

        teacherRepository.save(teacher);

        Account account = Account.builder()
                .email(registerRequestDTO.email())
                .username(registerRequestDTO.username())
                .password(passwordEncoder.encode(registerRequestDTO.password()))
                .state(RegisterState.WAITING)
                .person(teacher)
                .build();
        teacher.setAccount(account);

        accountRepository.save(account);

        teacherRepository.save(teacher);
    }

    private void groupManagerRegister(RegisterRequestDTO registerRequestDTO, Role role) {
        GroupManager groupManager = GroupManager.builder()
                .firstName(registerRequestDTO.firstName())
                .lastName(registerRequestDTO.lastName())
                .nationalCode(registerRequestDTO.nationalCode())
                .roles(Collections.singletonList(role))
                .build();

        groupMangerRepository.save(groupManager);

        Account account = Account.builder()
                .email(registerRequestDTO.email())
                .username(registerRequestDTO.username())
                .password(passwordEncoder.encode(registerRequestDTO.password()))
                .state(RegisterState.WAITING)
                .person(groupManager)
                .build();
        groupManager.setAccount(account);

        accountRepository.save(account);

        groupMangerRepository.save(groupManager);
    }


}
