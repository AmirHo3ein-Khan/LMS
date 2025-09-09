package ir.lms.service.impl;

import ir.lms.exception.RoleNotFoundException;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.service.InitializerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InitializerServiceImpl implements InitializerService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public InitializerServiceImpl(RoleRepository roleRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createAdminIfNotExists() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));

        if (personRepository.existsByRolesContains(adminRole)) {

            Person admin = Person.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .nationalCode("123456789")
                    .roles(Set.of(adminRole , userRole))
                    .build();

            Account account = Account.builder()
                    .email("admin@gmail.com")
                    .username("adminusername")
                    .password(passwordEncoder.encode("adminadmin"))
                    .state(RegisterState.CONFIRM)
                    .person(admin)
                    .activeRole(userRole)
                    .build();

            admin.setAccount(account);
            personRepository.save(admin);
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
}
