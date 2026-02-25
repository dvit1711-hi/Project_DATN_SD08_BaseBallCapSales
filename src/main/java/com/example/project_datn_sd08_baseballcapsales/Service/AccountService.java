package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService{
    @Autowired
    private AccountRepository accountRepository;

    public List<GetAccountDto> getAlladdressDtos() {
        return accountRepository.findAll()
                .stream()
                .map(GetAccountDto::new)
                .toList();
    }

    // Thêm Address mới
    public Account postAccount(PostAccountDto dto) {

        Account account = new Account();
        account.setEmail(dto.getAccountCode());
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());


        return accountRepository.save(account);
    }

}
