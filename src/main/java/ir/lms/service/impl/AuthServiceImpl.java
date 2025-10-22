package ir.lms.service.impl;

import ir.lms.util.dto.AuthRequestDTO;
import ir.lms.exception.AccessDeniedException;
import ir.lms.exception.EntityNotFoundException;
import ir.lms.model.*;
import ir.lms.model.enums.RegisterState;
import ir.lms.repository.*;
import ir.lms.service.AuthService;
import ir.lms.config.JwtService;
import ir.lms.util.dto.AuthenticationResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class AuthServiceImpl implements AuthService {

    private final static String NOT_FOUND = "%s not found!";

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;

    protected AuthServiceImpl(AuthenticationManager authenticationManager,
                              AccountRepository accountRepository,
                              JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }


    @Override
    public AuthenticationResponse login(AuthRequestDTO request) {
        final Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = (UserDetails) auth.getPrincipal();

        final Account account = accountRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(NOT_FOUND, "Account")));

        if (account.getState().equals(RegisterState.ACTIVE)) {

            account.setAuthId(UUID.randomUUID());
            accountRepository.save(account);

            final String token = jwtService.generateAccessToken(account.getAuthId());

            final String refreshToken = jwtService.generateRefreshToken(account.getAuthId());

            return AuthenticationResponse.builder().accessToken(token)
                    .refreshToken(refreshToken).tokenType("Barrier ").build();
        }

        throw new AccessDeniedException("You don't have access. Your Account not active!");
    }


    @Override
    public void logOut(String token) {
        String extractUUID = jwtService.extractUUID(token);

        Account account = accountRepository.findByAuthId(UUID.fromString(extractUUID))
                .orElseThrow(() -> new EntityNotFoundException("account.not.found"));

        account.setAuthId(null);
        accountRepository.save(account);
    }

}
