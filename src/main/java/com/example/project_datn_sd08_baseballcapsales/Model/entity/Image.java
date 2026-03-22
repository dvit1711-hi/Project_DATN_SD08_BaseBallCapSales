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

@Entity
@Table(name = "Images")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Image {
    @Id
    @Column(name = "imageID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "productColorID", nullable = false)
    private ProductColor productColorID;

    @Size(max = 255)
    @Nationalized
    @Column(name = "imageUrl")
    private String imageUrl;

    @ColumnDefault("0")
    @Column(name = "isMain")
    private Boolean isMain;


}