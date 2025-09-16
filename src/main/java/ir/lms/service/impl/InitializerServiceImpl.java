package ir.lms.service.impl;

import ir.lms.exception.AlreadyExistException;
import ir.lms.model.Account;
import ir.lms.model.Major;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.MajorRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.service.InitializerService;
import ir.lms.exception.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InitializerServiceImpl implements InitializerService {

    private final static String NOT_FOUND = "%s not found!";

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final MajorRepository majorRepository;

    public InitializerServiceImpl(RoleRepository roleRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository, MajorRepository majorRepository) {
        this.roleRepository = roleRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.majorRepository = majorRepository;
    }

    @Override
    public void createAdminIfNotExists() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND , "Role")));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND , "Role")));


        if (!personRepository.existsByRolesContains(adminRole)) {

            Person admin = Person.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .nationalCode("123456789")
                    .phoneNumber("09123324213")
                    .roles(List.of(adminRole, userRole))
                    .build();

            personRepository.save(admin);


            Account account = Account.builder()
                    .username("adminusername")
                    .password(passwordEncoder.encode("adminadmin"))
                    .state(RegisterState.ACTIVE)
                    .person(admin)
                    .activeRole(userRole)
                    .build();

            admin.setAccount(account);
            accountRepository.save(account);
        }
    }

    @Override
    public void createRolesIfNotExist() {
        Optional<Role> user = roleRepository.findByName("USER");
        Optional<Role> admin = roleRepository.findByName("ADMIN");
        Optional<Role> manager = roleRepository.findByName("MANAGER");
        Optional<Role> teacher = roleRepository.findByName("TEACHER");
        Optional<Role> student = roleRepository.findByName("STUDENT");
        if (student.isEmpty() && teacher.isEmpty() && manager.isEmpty() && admin.isEmpty() && user.isEmpty()) {
            roleRepository.save(Role.builder().name("USER").build());
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("MANAGER").build());
            roleRepository.save(Role.builder().name("TEACHER").build());
            roleRepository.save(Role.builder().name("STUDENT").build());
        }
    }

    @Override
    public void createMajorIfNotExists() {
        Major computer = Major.builder().majorName("Computer")
                .active(true).majorCode(UUID.randomUUID()).build();
        if (majorRepository.findByMajorName(computer.getMajorName()).isEmpty()) {
            majorRepository.save(computer);
        }
    }
}
