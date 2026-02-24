package com.example.project_datn_sd08_baseballcapsales.Service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Stream;

public class authService {
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
}
