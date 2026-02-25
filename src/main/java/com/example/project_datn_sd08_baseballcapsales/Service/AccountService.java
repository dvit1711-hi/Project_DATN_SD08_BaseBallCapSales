package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        account.setEmail(dto.getEmail());
        account.setUsername(dto.getUsername());
        account.setPassword(dto.getPassword());
        return accountRepository.save(account);
    }

    // Lấy thông tin người dùng
    public List<GetAccountDto> getAllAccountDto() {
        return accountRepository.findAll().stream()
                .map(account -> new GetAccountDto(
                        account.getId(),
                        account.getUsername(),
                        account.getPassword(),
                        account.getEmail(),
                        account.getPhoneNumber(),
                        account.getImages(),
                        account.getCreateDate()
                ))
                .collect(Collectors.toList());
    }

    // Thêm thông tin người dùng mới
    public Account createAccount(PostAccountDto dto) {
        Account acc = new Account();
        acc.setUsername(dto.getUsername());
        acc.setEmail(dto.getEmail());
        acc.setPassword(dto.getPassword());
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setImages(dto.getImages());
        return accountRepository.save(acc);
    }

    // Sửa thông tin người dùng
    public Account updateAccount(Integer id ,PutAccountDto dto) {
        Optional<Account> account = accountRepository.findById(id);
        if(!account.isPresent()) {
            throw new IllegalArgumentException("Account not found");
        }
        Account acc = account.get();
        acc.setUsername(dto.getUsername());
        acc.setEmail(dto.getEmail());
        acc.setPassword(dto.getPassword());
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setImages(dto.getImages());
        return accountRepository.save(acc);
    }

    public boolean deleteAccount(Integer id) {
        if(accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found");
        }
        accountRepository.deleteById(id);
        return true;
    }

}
