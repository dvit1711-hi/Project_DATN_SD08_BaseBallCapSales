package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Colors")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Color {
    @Id
    @Column(name = "colorID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "colorName", length = 50)
    private String colorName;

    @Size(max = 20)
    @Nationalized
    @Column(name = "colorCode", length = 20)
    private String colorCode;


}