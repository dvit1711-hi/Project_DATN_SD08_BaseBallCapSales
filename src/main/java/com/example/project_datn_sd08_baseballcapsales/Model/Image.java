package com.example.project_datn_sd08_baseballcapsales.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Images")
public class Image {
    @Id
    @Column(name = "imageID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "productColorID", nullable = false)
    private com.example.project_datn_sd08_baseballcapsales.Model.ProductColor productColorID;

    @Size(max = 255)
    @Nationalized
    @Column(name = "imageUrl")
    private String imageUrl;

    @ColumnDefault("0")
    @Column(name = "isMain")
    private Boolean isMain;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.example.project_datn_sd08_baseballcapsales.Model.ProductColor getProductColorID() {
        return productColorID;
    }

    public void setProductColorID(com.example.project_datn_sd08_baseballcapsales.Model.ProductColor productColorID) {
        this.productColorID = productColorID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

}