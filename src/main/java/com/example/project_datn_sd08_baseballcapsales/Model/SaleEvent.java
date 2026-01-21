package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "SaleEvents")
public class SaleEvent {
    @Id
    @Column(name = "saleID", nullable = false)
    private Integer id;

    @Size(max = 100)
    @Nationalized
    @Column(name = "saleName", length = 100)
    private String saleName;

    @Column(name = "startDate")
    private LocalDate startDate;

    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "discountPercent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Size(max = 20)
    @Nationalized
    @Column(name = "Status", length = 20)
    private String status;

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

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderDetail getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(OrderDetail orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

}