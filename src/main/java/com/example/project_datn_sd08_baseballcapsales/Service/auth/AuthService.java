package com.example.project_datn_sd08_baseballcapsales.Service.auth;

import com.example.project_datn_sd08_baseballcapsales.payload.request.LoginRequest;
import com.example.project_datn_sd08_baseballcapsales.payload.reponse.LoginResponse;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.jwt.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AccountRepository accountRepository;

    public AuthService(AuthenticationManager authenticationManager, JWTService jwtService, AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
    }

    public LoginResponse login(LoginRequest loginRequest){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),     // login bằng email
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Tạo token
        String accessToken = jwtService.generateToken(userDetails);

        // Lấy account theo email
        Account account = accountRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));

        // Lấy role từ AccountRole
        Set<String> roles = account.getAccountRoles()
                .stream()
                .map(ar -> ar.getRole().getRoleName())
                .collect(Collectors.toSet());

        return new LoginResponse(
                accessToken,
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                roles
        );
    }
}