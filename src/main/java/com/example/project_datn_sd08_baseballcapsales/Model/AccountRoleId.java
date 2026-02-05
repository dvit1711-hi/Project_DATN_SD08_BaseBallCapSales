package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountRoleId implements Serializable {
    private static final long serialVersionUID = -6410856247258538681L;
    @NotNull
    @Column(name = "accountID", nullable = false)
    private Integer accountID;

    @NotNull
    @Column(name = "roleID", nullable = false)
    private Integer roleID;

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccountRoleId entity = (AccountRoleId) o;
        return Objects.equals(this.accountID, entity.accountID) &&
                Objects.equals(this.roleID, entity.roleID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID, roleID);
    }

}