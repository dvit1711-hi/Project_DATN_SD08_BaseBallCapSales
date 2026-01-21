package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "Payments")
public class Payment {
    @Id
    @Column(name = "paymentID", nullable = false)
    private Integer id;

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

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderDetailsID", nullable = false)
    private OrderDetail orderDetailsID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public OrderDetail getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(OrderDetail orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

}