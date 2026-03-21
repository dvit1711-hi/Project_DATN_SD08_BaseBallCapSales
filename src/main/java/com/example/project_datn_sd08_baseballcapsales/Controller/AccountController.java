package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.AccountDto.AccountUpdateDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutStatusDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAccountDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.AddressRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.AccountService;
import com.example.project_datn_sd08_baseballcapsales.payload.request.ChangePasswordRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
//    @Autowired
//    private AddressService addressService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AddressRepository addressRepository;

    // Địa chỉ
    @GetMapping
    public List<GetAccountDto> getAccountDtos() {
        return accountService.getAlladdressDtos();
    }

    @GetMapping("/{id}")
    public GetAccountDto getAccountById(@PathVariable Integer id){
        return accountService.getAccountById(id);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {

        Account acc = accountRepository.findById(id).orElse(null);
        Address address = addressRepository.findByAccount_Id(id);

        Map<String, Object> result = new HashMap<>();
        result.put("account", acc);
        result.put("address", address);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Account> postAccountDto(@Valid @RequestBody PostAccountDto postAccountDto) {
        Account account =accountService.postAccount(postAccountDto);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/update-full")
    public ResponseEntity<?> updateFull(@RequestBody AccountUpdateDto dto) {
        accountService.updateFull(dto);
        return ResponseEntity.ok("Updated successfully");
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id,
                                          @RequestBody PutStatusDto dto) {
        accountService.updateStatus(id, dto);
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
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
