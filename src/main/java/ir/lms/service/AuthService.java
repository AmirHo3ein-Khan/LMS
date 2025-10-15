package ir.lms.service;

import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO authRequestDTO);
    void logOut(String token);
}
