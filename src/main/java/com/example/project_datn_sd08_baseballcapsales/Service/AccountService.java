package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.AccountDto.AccountUpdateDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;

import com.example.project_datn_sd08_baseballcapsales.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class AccountService{
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AddressRepository addressRepository;

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

        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found");
        }

        accountRepository.deleteById(id);
        return true;
    }

    public Account getAccountById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateFull(AccountUpdateDto dto) {

        // lấy Account object
        Account acc = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // === UPDATE ACCOUNT ===
        acc.setUsername(dto.getUsername());
        acc.setEmail(dto.getEmail());
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setImages(dto.getImages());
        accountRepository.save(acc);

        // === UPDATE ADDRESS ===
        Address address = addressRepository.findByAccount_Id(dto.getAccountId());

        if (address == null) {
            address = new Address();
            address.setAccount(acc);  // gán OBJECT Account
        }

        address.setUnitNumber(dto.getUnitNumber());
        address.setStreetNumber(dto.getStreetNumber());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setRegion(dto.getRegion());
        address.setPostalCode(dto.getPostalCode());

        addressRepository.save(address);
    }
}
