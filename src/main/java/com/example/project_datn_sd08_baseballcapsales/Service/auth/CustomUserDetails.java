package com.example.project_datn_sd08_baseballcapsales.Service.auth;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

//public class CustomUserDetails implements UserDetails {
//    private Account account;
//
//    public CustomUserDetails(Account account) {
//        this.account = account;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return account.getAccountRoles().stream()
//                .map(ar -> new SimpleGrantedAuthority(ar.getRoleID().getRoleName()))
//                .toList();
//    }
//
//    @Override
//    public String getPassword() {
//        return account.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return account.getUsername();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() { return true; }
//
//    @Override
//    public boolean isAccountNonLocked() { return true; }
//
//    @Override
//    public boolean isCredentialsNonExpired() { return true; }
//
//    @Override
//    public boolean isEnabled() { return true; }
//
//    // OPTIONAL — getter
//    public Integer getAccountID() {
//        return account.getId();
//    }
//
//    public String getEmail() {
//        return account.getEmail();
//    }
//}
