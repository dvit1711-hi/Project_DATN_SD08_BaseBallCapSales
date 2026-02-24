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

    @MapsId("accountID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private Account accountID;

    @MapsId("roleID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roleID", nullable = false)
    private Role roleID;


}