package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.AccountRole;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountRolesService accountRolesService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<AccountRole> rolesList = accountRolesService.findByUser(account);

        List<SimpleGrantedAuthority> authorities = rolesList.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getRoleName()))
                .toList();

        String statusName = account.getStatus() != null && account.getStatus().getStatusName() != null
                ? account.getStatus().getStatusName().trim()
                : "";

        boolean isLocked = "Locked".equalsIgnoreCase(statusName);

        return org.springframework.security.core.userdetails.User
                .withUsername(account.getEmail())
                .password(account.getPassword() != null ? account.getPassword() : "")
                .authorities(authorities)
                .accountLocked(isLocked)
                .disabled(false)
                .build();
    }
}