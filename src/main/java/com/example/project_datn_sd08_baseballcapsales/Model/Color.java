package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Colors")
public class Color {
    @Id
    @Column(name = "colorID", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "colorName", length = 50)
    private String colorName;

    @Size(max = 20)
    @Nationalized
    @Column(name = "colorCode", length = 20)
    private String colorCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

}