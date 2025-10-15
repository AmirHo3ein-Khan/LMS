package ir.lms.service;

import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.service.base.BaseService;
import ir.lms.util.dto.ChangePassDTO;

import java.security.Principal;
import java.util.List;

public interface PersonService extends BaseService<Person, Long>  {
    void changeRole(String username , String roleName);
    void addRoleToPerson(String role , Long personId);
    void activeAccount(Long id);
    void inactiveAccount(Long id);
    List<Role> getPersonRoles(Principal principal);
    void changePassword(ChangePassDTO dto, Principal principal);
    void updateProfile(Person person, Principal principal);
    List<Person> findAllByMajor(String majorName);
    List<Person> search(String keyword);
}
