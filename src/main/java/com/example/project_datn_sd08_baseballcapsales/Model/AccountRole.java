package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "AccountRoles")
public class AccountRole {
    @EmbeddedId
    private AccountRoleId id;

    @MapsId("accountId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Account account;

    @MapsId("roleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Role role;

    public AccountRoleId getId() {
        return id;
    }

    public void setId(AccountRoleId id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Account getAccount() {
        return account;
    }

    public void setAccount(com.example.project_datn_sd08_baseballcapsales.Model.Account account) {
        this.account = account;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Role getRole() {
        return role;
    }

    public void setRole(com.example.project_datn_sd08_baseballcapsales.Model.Role role) {
        this.role = role;
    }

}