package ir.lms.service.impl;

import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.DuplicateException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.AccountRepository;
import ir.lms.repository.PersonRepository;
import ir.lms.repository.RoleRepository;
import ir.lms.service.PersonService;
import ir.lms.service.base.BaseServiceImpl;
import ir.lms.util.PersonSpecification;
import ir.lms.util.dto.ChangePassDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl  extends BaseServiceImpl<Person, Long> implements PersonService {

    private final static String NOT_FOUND = "%s not found!";
    private final static String EXIST = "Already exists!";

    private final AccountRepository accountRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public PersonServiceImpl(JpaRepository<Person, Long> repository, AccountRepository accountRepository,
                             PersonRepository personRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.accountRepository = accountRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Account")));
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
    public void changePassword(ChangePassDTO dto, Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "account")));
        if (passwordEncoder.matches(dto.getOldPassword(), account.getPassword())) {
            account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            accountRepository.save(account);
        } else
            throw new AccessDeniedException("Old password doesn't match");
    }

    @Override
    public void updateProfile(Person person, Principal principal) {
        Account account = accountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));
        account.getPerson().setFirstName(person.getFirstName());
        account.getPerson().setLastName(person.getLastName());
        account.getPerson().setPhoneNumber(person.getPhoneNumber());
        personRepository.save(account.getPerson());
    }

    @Override
    public List<Person> findAllByMajor(String majorName) {
        return personRepository.findAllByMajor_MajorName(majorName);
    }

    @Override
    public List<Person> search(String keyword) {
        Specification<Person> personSpecification = PersonSpecification.searchPersonByKeyword(keyword);
        return personRepository.findAll(personSpecification);
    }


    @Override
    public Person update(Long aLong, Person person) {
        Person foundedPerson = personRepository.findById(aLong)
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Person")));
        foundedPerson.setFirstName(person.getFirstName());
        foundedPerson.setLastName(person.getLastName());
        foundedPerson.setNationalCode(person.getNationalCode());
        foundedPerson.setPhoneNumber(person.getPhoneNumber());
        return personRepository.save(foundedPerson);
    }

    @Override
    protected void prePersist(Person person) {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND, "Role")));
        if (personRepository.existsByNationalCodeAndPhoneNumber(person.getNationalCode() , person.getPhoneNumber())) {
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
