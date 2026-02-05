package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "AccountRoles")
public class AccountRole {
    @EmbeddedId
    private AccountRoleId id;

    @MapsId("accountID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Account accountID;

    @MapsId("roleID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roleID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.Role roleID;

    public AccountRoleId getId() {
        return id;
    }

    public void setId(AccountRoleId id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Account getAccountID() {
        return accountID;
    }

    public void setAccountID(com.example.project_datn_sd08_baseballcapsales.Model.Account accountID) {
        this.accountID = accountID;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.Role getRoleID() {
        return roleID;
    }

    public void setRoleID(com.example.project_datn_sd08_baseballcapsales.Model.Role roleID) {
        this.roleID = roleID;
    }

}