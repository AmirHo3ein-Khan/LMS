package ir.lms.service;

import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.util.dto.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse login(AuthRequestDTO authRequestDTO);
    void logOut(String token);
}
