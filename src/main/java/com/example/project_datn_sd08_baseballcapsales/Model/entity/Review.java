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

@Entity
@Table(name = "Reviews")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Review {
    @Id
    @Column(name = "reviewID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productID", nullable = false)
    private Product productID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "accountID", nullable = false)
    private Account accountID;

    @Column(name = "rating")
    private Integer rating;

    @Size(max = 500)
    @Nationalized
    @Column(name = "comment", length = 500)
    private String comment;

    @ColumnDefault("getdate()")
    @Column(name = "createdAt")
    private Instant createdAt;


}