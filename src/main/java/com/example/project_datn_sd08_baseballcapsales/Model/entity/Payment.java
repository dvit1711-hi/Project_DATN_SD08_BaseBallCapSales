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

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Payment {
    @Id
    @Column(name = "paymentID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderID", nullable = false)
    private Order orderID;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Size(max = 50)
    @Nationalized
    @Column(name = "method", length = 50)
    private String method;

    @Size(max = 50)
    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @ColumnDefault("getdate()")
    @Column(name = "createdAt")
    private Instant createdAt;


}