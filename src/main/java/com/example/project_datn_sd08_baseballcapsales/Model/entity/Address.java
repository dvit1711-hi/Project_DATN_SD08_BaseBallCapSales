package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Address")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Address {
    @Id
    @Column(name = "addressID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID")
    private Account accountID;

}