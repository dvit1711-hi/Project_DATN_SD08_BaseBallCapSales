package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.AccountRoleId;
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

        // tạo account
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setEmail(request.getEmail());

        account = accountRepository.save(account); // lấy lại account có ID

        // lấy role USER
        Role role = roleRepository.findByRoleName("ROLE_USER");

        // tạo khóa chính
        AccountRoleId id = new AccountRoleId();
        id.setAccountID(account.getId());
        id.setRoleID(role.getId());

        // tạo accountRole
        AccountRole accountRole = new AccountRole();
        accountRole.setId(id);
        accountRole.setAccount(account);
        accountRole.setRole(role);

        accountRoleRepository.save(accountRole);
    }
}
