package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
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

    public GetAccountDto getAccountById(Integer id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account id " + id + " not found"));

        return new GetAccountDto(account);
    }

    public List<GetAccountDto> getAlladdressDtos() {
        return accountRepository.findAll()
                .stream()
                .map(GetAccountDto::new)
                .toList();
    }

    // Thêm Address mới
    public Account postAccount(PostAccountDto dto) {
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());
        return accountRepository.save(account);
    }

    public Account updateAccount(Integer id, PutAccountDto dto) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setPassword(dto.getPassword());
        account.setPhoneNumber(dto.getPhoneNumber());
        account.setImages(dto.getImages());

        return accountRepository.save(account);
    }

    public boolean deleteAccount(Integer id) {
        if(accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepository.deleteById(id);
        return true;
    }

}
