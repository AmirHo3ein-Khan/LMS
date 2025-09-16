package ir.lms.service;

import ir.lms.dto.auth.AuthRequestDTO;
import ir.lms.dto.auth.AuthResponseDTO;
import ir.lms.dto.auth.ChangeRoleRequestDTO;
import ir.lms.model.Person;
import ir.lms.model.Role;
import ir.lms.service.base.BaseService;

import java.security.Principal;
import java.util.List;

public interface AuthService extends BaseService<Person, Long> {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    void changeRole(String username , String roleName);
    void addRoleToPerson(String role , Long personId);
    void activeAccount(Long id);
    void inactiveAccount(Long id);
    List<Role> getPersonRoles(Principal principal);
}
