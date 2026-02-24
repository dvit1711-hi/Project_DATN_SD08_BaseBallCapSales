package com.example.project_datn_sd08_baseballcapsales.Service.auth;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
//
//public class CustomUserDetailsService implements UserDetailsService {
////    @Autowired
////    private AccountRepository accountRepository;
////
////    @Override
////    public UserDetails loadUserByUsername(String username)
////            throws UsernameNotFoundException {
////
////        Account acc = accountRepository.findByUsername(username)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
////
////        // Lấy roles từ bảng AccountRoles
////        List<String> roles = acc.getAccountRoles()
////                .stream()
////                .map(ar -> ar.getRoleID().getRoleName().replace("ROLE_", "")) // bỏ tiền tố nếu cần
////                .toList();
////
////        return org.springframework.security.core.userdetails.User
////                .withUsername(acc.getUsername())
////                .password(acc.getPassword())
////                .roles(roles.toArray(new String[0]))
////                .build();
////    }
//}
