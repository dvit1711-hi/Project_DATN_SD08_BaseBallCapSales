package com.example.project_datn_sd08_baseballcapsales.Model.dto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Address;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class GetAddressDto {
    @Size(max = 20)
    @Nationalized
    private String unitNumber;

    @Size(max = 20)
    @Nationalized
    private String streetNumber;

    @Size(max = 255)
    @Nationalized
    private String addressLine1;

    @Size(max = 255)
    @Nationalized
    private String addressLine2;

    @Size(max = 100)
    @Nationalized
    private String city;

    @Size(max = 100)
    @Nationalized
    private String region;

    @Size(max = 20)
    @Nationalized
    private String postalCode;

    @Size(max = 50)
    @NotNull
    @Nationalized
    private String accountCode;

    public GetAddressDto(Address address) {
        this.unitNumber = address.getUnitNumber();
        this.streetNumber = address.getStreetNumber();
        this.addressLine1 = address.getAddressLine1();
        this.addressLine2 = address.getAddressLine2();
        this.city = address.getCity();
        this.region = address.getRegion();
        this.postalCode = address.getPostalCode();
        if(address.getAccountID() != null){
            this.accountCode = address.getAccountID().getEmail();
        }
    }
}
