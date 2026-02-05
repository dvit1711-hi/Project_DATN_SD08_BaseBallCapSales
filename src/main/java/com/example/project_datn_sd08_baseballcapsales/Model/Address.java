package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
public class Address {
    @Id
    @Column(name = "addressID", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Nationalized
    @Column(name = "unit_number", length = 20)
    private String unitNumber;

    @Size(max = 20)
    @Nationalized
    @Column(name = "street_number", length = 20)
    private String streetNumber;

    @Size(max = 255)
    @Nationalized
    @Column(name = "address_line1")
    private String addressLine1;

    @Size(max = 255)
    @Nationalized
    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 100)
    @Nationalized
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100)
    @Nationalized
    @Column(name = "region", length = 100)
    private String region;

    @Size(max = 20)
    @Nationalized
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private Account accountID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Account getAccountID() {
        return accountID;
    }

    public void setAccountID(Account accountID) {
        this.accountID = accountID;
    }

}