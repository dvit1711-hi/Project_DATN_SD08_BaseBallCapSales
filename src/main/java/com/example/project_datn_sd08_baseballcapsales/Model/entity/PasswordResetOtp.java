package com.example.project_datn_sd08_baseballcapsales.Model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PasswordResetOtps")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resetOtpID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID", nullable = false)
    private Account account;

    @Column(name = "otpHash", nullable = false, length = 255)
    private String otpHash;

    @Column(name = "expiryAt", nullable = false)
    private LocalDateTime expiryAt;

    @Column(name = "used", nullable = false)
    private Boolean used = false;

    @Column(name = "attemptCount", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "usedAt")
    private LocalDateTime usedAt;
}