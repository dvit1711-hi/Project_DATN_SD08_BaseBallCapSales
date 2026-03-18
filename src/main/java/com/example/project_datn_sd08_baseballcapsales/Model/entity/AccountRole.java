package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "AccountRoles")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AccountRole {

    @EmbeddedId
    private AccountRoleId id;

    @ManyToOne
    @MapsId("accountID")
    @JoinColumn(name = "accountID")
    private Account account;

    @ManyToOne
    @MapsId("roleID")
    @JoinColumn(name = "roleID")
    private Role role;
}