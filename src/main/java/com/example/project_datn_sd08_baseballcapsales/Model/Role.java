package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleID")
    private Integer id;

    @Column(name = "roleName")
    private String roleName;

    public Integer getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}

