package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Brands")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Brand {
    @Id
    @Column(name = "brandID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer brandID;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "name", nullable = false, length = 100)
    private String name;


}