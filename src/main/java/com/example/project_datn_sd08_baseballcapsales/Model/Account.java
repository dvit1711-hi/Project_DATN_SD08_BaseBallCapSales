package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountID", nullable = false)
    private Integer id;

    @NotNull
    @Size(max = 50)
    @Nationalized
    @Column(name = "account_code", nullable = false, length = 50)
    private String accountCode;

    @NotNull
    @Size(max = 50)
    @Nationalized
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @NotNull
    @Size(max = 255)
    @Nationalized
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100)
    @Nationalized
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20)
    @Nationalized
    @Column(name = "phoneNumber", length = 20)
    private String phoneNumber;

    @Size(max = 255)
    @Nationalized
    @Column(name = "images")
    private String images;

    // ===== ROLE MAPPING =====
    @OneToMany(
            mappedBy = "account",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    private Set<AccountRole> accountRoles = new HashSet<>();


    // ===== GETTERS / SETTERS =====

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    // ===== LOGIC ROLE – GIỮ NGUYÊN CHO SECURITY =====
    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        if (accountRoles != null) {
            for (AccountRole ar : accountRoles) {
                roles.add(ar.getRole());
            }
        }
        return roles;
    }
}
