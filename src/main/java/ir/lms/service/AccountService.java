package ir.lms.service;

import ir.lms.model.dto.*;

public interface AccountService {
    ApiResponseDto registerAccount(RegisterRequestDTO registerRequestDTO);
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    ApiResponseDto addRoleToPerson(AddRoleRequest request);

}
