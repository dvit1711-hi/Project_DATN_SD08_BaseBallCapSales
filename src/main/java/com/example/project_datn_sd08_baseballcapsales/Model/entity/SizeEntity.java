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
@Table(name = "Sizes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SizeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sizeID", nullable = false)
    private Integer sizeID;

    @Size(max = 50)
    @Nationalized
    @Column(name = "sizeName", length = 50, nullable = false)
    private String sizeName;

    @Size(max = 200)
    @Nationalized
    @Column(name = "sizeDescription", length = 200)
    private String sizeDescription;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @OneToMany(mappedBy = "sizeID")
    private List<ProductColor> productColors;
}