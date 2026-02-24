package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Carts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Cart {
    @Id
    @Column(name = "cartID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private Account accountID;


}