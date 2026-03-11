package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.payload.request.RegisterRequest;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.AccountRole;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Role;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRoleRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountRolesService {
    @Autowired
    AccountRoleRepository accountRoleRepository;

    public List<AccountRole> findByUser(Account account) {
        return accountRoleRepository.findByAccount(account);
    }

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {

        // 1. Tạo Account
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setEmail(request.getEmail());

        // 🔐 encode password
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        Account savedAccount = accountRepository.save(account);

        // 2. Lấy role USER
        Role roleUser = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        // 3. Tạo AccountRole
        AccountRole accountRole = new AccountRole();
        accountRole.setAccount(savedAccount);
        accountRole.setRole(roleUser);

        accountRoleRepository.save(accountRole);
    }
}
