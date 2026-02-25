package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.GetAddressDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostAddressDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
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
        account.setAccountCode(dto.getAccountCode());
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());


        return accountRepository.save(account);
    }

}
