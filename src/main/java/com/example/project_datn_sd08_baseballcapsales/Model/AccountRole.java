package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "AccountRoles")
public class AccountRole {

    @EmbeddedId
    private AccountRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    public com.example.project_datn_sd08_baseballcapsales.Model.Role getRole() {
        return role;
    }

    public void setRole(com.example.project_datn_sd08_baseballcapsales.Model.Role role) {
        this.role = role;
    }
}

