package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(name = "Materials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "materialID", nullable = false)
    private Integer materialID;

    @Size(max = 100)
    @Nationalized
    @Column(name = "materialName", length = 100, nullable = false)
    private String materialName;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "materialID")
    private List<Product> products;
}