package ir.lms.service.impl;

import ir.lms.exception.AdminAlreadyExistException;
import ir.lms.exception.RoleNotFoundException;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.service.InitializerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InitializerServiceImpl implements InitializerService {
    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public InitializerServiceImpl(RoleRepository roleRepository, AccountRepository accountRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createAdminIfNotExists() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));

        boolean adminExists = personRepository.existsByRolesContaining(adminRole);
        if (!adminExists) {
            Person admin = Person.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .nationalCode("123456789")
                    .roles(Collections.singletonList(adminRole))
                    .build();

            personRepository.save(admin);

            Account account = Account.builder()
                    .email("admin@gmail.com")
                    .username("adminadmin")
                    .password(passwordEncoder.encode("adminadmin"))
                    .state(RegisterState.CONFIRM)
                    .person(admin)
                    .build();


            admin.setAccount(account);

            accountRepository.save(account);

            personRepository.save(admin);
        }
    }

    @Override
    public void createRolesIfNotExist() {
        Optional<Role> student = roleRepository.findByName("STUDENT");
        Optional<Role> master = roleRepository.findByName("TEACHER");
        Optional<Role> manager = roleRepository.findByName("MANAGER");
        Optional<Role> admin = roleRepository.findByName("ADMIN");
        if (student.isEmpty() && master.isEmpty() && manager.isEmpty() && admin.isEmpty()) {
            roleRepository.save(Role.builder().name("STUDENT").build());
            roleRepository.save(Role.builder().name("TEACHER").build());
            roleRepository.save(Role.builder().name("MANAGER").build());
            roleRepository.save(Role.builder().name("ADMIN").build());
        }
    }
}
