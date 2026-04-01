package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.AccountDto.AccountUpdateDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutStatusDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetStatusDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Status;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.AddressRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.StatusRepository;
import com.example.project_datn_sd08_baseballcapsales.payload.request.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatusRepository statusRepository;

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

    public Account postAccount(PostAccountDto dto) {
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setUsername(dto.getUsername());

        // nên mã hóa password khi tạo mới
        account.setPassword(passwordEncoder.encode(dto.getPassword()));

        return accountRepository.save(account);
    }

    public Account updateAccount(Integer id, PutAccountDto dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setUsername(dto.getUsername());
        account.setEmail(dto.getEmail());
        account.setPhoneNumber(dto.getPhoneNumber());
        account.setImages(dto.getImages());

        // chỉ update password nếu frontend có gửi password mới
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            account.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

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
        Account acc = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // UPDATE ACCOUNT
        acc.setUsername(dto.getUsername());
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setImages(dto.getImages());
        accountRepository.save(acc);

        // UPDATE ADDRESS
        Address address = addressRepository.findByAccount_Id(dto.getAccountId());

        if (address == null) {
            address = new Address();
            address.setAccount(acc);
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

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập.");
        }

        String email = authentication.getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản."));

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mật khẩu hiện tại.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập mật khẩu mới.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không đúng.");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới không được trùng mật khẩu cũ.");
        }

//        if (request.getNewPassword().length() < 8) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải có ít nhất 8 ký tự.");
//        }
//
//        if (!request.getNewPassword().matches(".*[A-Z].*")) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải có ít nhất 1 chữ hoa.");
//        }
//
//        if (!request.getNewPassword().matches(".*[a-z].*")) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải có ít nhất 1 chữ thường.");
//        }
//
//        if (!request.getNewPassword().matches(".*\\d.*")) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới phải có ít nhất 1 chữ số.");
//        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    public List<GetStatusDto> getAllStatuses() {
        return statusRepository.findAll()
                .stream()
                .map(GetStatusDto::new)
                .toList();
    }

    @Transactional
    public void updateStatus(Integer accountId, PutStatusDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Status status = statusRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        account.setStatus(status);
        accountRepository.save(account);
    }
}