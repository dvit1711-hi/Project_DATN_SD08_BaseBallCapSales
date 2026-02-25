package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostAddressDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutAddressDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetAddressDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping
    public List<GetAddressDto> getAddressDtos() {
        return addressService.getAlladdressDtos();
    }

    @PostMapping
    public ResponseEntity<Address> postProduct(@Valid @RequestBody PostAddressDto dto){
        Address address =addressService.postAddress(dto);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putDonHang(@PathVariable Integer id,@Valid @RequestBody PutAddressDto dto){
        Address address = addressService.putAddress(id, dto);
        if (address == null) {
            return ResponseEntity
                    .status(404)
                    .body("dia chi Not Found");
        }
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonHang(@PathVariable Integer id){
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

}
