package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AccountRepository accountRepository;

    public List<GetAddressDto> getAlladdressDtos() {
        return addressRepository.findAll()
                .stream()
                .map(GetAddressDto::new)
                .toList();
    }

    // Thêm Address mới
    public Address postAddress(PostAddressDto dto) {

        Account account = accountRepository.findById(dto.getAccountID())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Address address = new Address();
        address.setUnitNumber(dto.getUnitNumber());
        address.setStreetNumber(dto.getStreetNumber());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setRegion(dto.getRegion());
        address.setPostalCode(dto.getPostalCode());
        address.setAccountID(account);

        return addressRepository.save(address);
    }

    // Cập nhật Address
    public Address putAddress(Integer id, PutAddressDto dto) {

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address id not found"));

        address.setUnitNumber(dto.getUnitNumber());
        address.setStreetNumber(dto.getStreetNumber());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setRegion(dto.getRegion());
        address.setPostalCode(dto.getPostalCode());

        // cập nhật account nếu có truyền vào
        if (dto.getAccountID() != null) {
            Account account = accountRepository.findById(dto.getAccountID())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            address.setAccountID(account);
        }

        return addressRepository.save(address);
    }

    // Xóa Address
    public boolean deleteAddress(Integer id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Address id " + id + " not found");
        }
        addressRepository.deleteById(id);
        return true;
    }
}
