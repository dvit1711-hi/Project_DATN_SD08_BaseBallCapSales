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

import java.util.List;
import java.util.stream.Stream;

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

    public Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public String getUsername() {
        return this.getAuthentication().getName();
    }

    public List<String> getRoles() {
        return this.getAuthentication().getAuthorities().stream()
                .map(au -> au.getAuthority().substring(5))
                .toList();
    }

    public boolean isAuthenticated() {
        String username = this.getUsername();
        return (username != null && !username.equals("anonymousUser"));
    }

    public boolean hasAnyRole(String... roles) {
        var grantedRoles = this.getRoles();
        return Stream.of(roles).anyMatch(grantedRoles::contains);
    }

    public LoginResponse login(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails= (UserDetails) authentication.getPrincipal();

        String accessToken= jwtService.generateToken(userDetails);
        String refreshToken= jwtService.generateRefreshToken(userDetails);
        Account account= accountRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return new LoginResponse(accessToken);
    }
}
