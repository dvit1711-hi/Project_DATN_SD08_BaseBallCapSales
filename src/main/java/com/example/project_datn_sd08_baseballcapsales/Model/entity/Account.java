package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "Accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {
    @Id
<<<<<<< HEAD
=======
    @Column(name = "accountID", nullable = false)
>>>>>>> 5bd85983ef87054ab079c769d21d34f862799154
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

    @ColumnDefault("getdate()")
    @Column(name = "createDate")
    private Instant createDate;

//    @OneToMany(mappedBy = "account")
//    private List<AccountRole> accountRoles;

}