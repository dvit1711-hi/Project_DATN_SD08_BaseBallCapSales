package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<GetAccountDto> getAccountDtos() {
        return accountService.getAlladdressDtos();
    }

    @PostMapping
    public ResponseEntity<Account> postAccountDto(@Valid @RequestBody PostAccountDto postAccountDto) {
        Account account =accountService.postAccount(postAccountDto);
        return ResponseEntity.ok(account);
    }
}
