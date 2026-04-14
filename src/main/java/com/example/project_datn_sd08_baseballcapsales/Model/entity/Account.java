package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "Accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
    @Column(name = "accountID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 20)
    @Nationalized
    @Column(name = "phoneNumber", length = 20)
    private String phoneNumber;

    @Nationalized
    @Column(name = "images", columnDefinition = "NVARCHAR(MAX)")
    private String images;

    @CreationTimestamp
    @Column(name = "createDate", updatable = false, nullable = false)
    private Instant createDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "statusID")
    private Status status;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<AccountRole> accountRoles;
}