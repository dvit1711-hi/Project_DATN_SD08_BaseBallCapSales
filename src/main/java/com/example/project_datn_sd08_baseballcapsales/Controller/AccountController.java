package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
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

    // Địa chỉ
    @GetMapping
    public List<GetAccountDto> getAccountDtos() {
        return accountService.getAlladdressDtos();
    }

    @PostMapping
    public ResponseEntity<Account> postAccountDto(@Valid @RequestBody PostAccountDto postAccountDto) {
        Account account =accountService.postAccount(postAccountDto);
        return ResponseEntity.ok(account);
    }

    // Thông tin người dùng
    @GetMapping
    public List<GetAccountDto> getAllAccount() {
        return accountService.getAlladdressDtos();
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody PostAccountDto dto) {
        Account acc = accountService.createAccount(dto);
        return ResponseEntity.ok(acc);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable Integer id, @Valid @RequestBody PutAccountDto dto) {
        Account update = accountService.updateAccount(id, dto);
        if (update == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Integer id) {
        boolean deleted = accountService.deleteAccount(id);
        if (!deleted) {
            return ResponseEntity
                    .status(404)
                    .body("Customer not found");
        }
        return ResponseEntity.ok().build();
    }
}
