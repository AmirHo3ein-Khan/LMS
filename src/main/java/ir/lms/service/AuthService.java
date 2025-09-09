package ir.lms.service;

import ir.lms.model.Account;
import ir.lms.model.Person;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.auth.*;

import java.util.List;

public interface AuthService {
    ApiResponseDTO studentRegister(RegisterRequestDTO registerRequestDTO);
    ApiResponseDTO teacherRegister(RegisterRequestDTO registerRequestDTO);
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    ApiResponseDTO addRoleToPerson(AddRoleRequest request);
    AuthResponseDTO changeRole(ChooseRoleRequestDTO request, String username);
    List<Account> getAccounts();
    List<Person> getPersons();


}
