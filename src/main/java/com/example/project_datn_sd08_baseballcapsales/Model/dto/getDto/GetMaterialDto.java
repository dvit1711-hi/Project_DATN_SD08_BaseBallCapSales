package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Material;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMaterialDto {

    private Integer materialID;
    private String materialName;

    public GetMaterialDto(Material material) {
        this.materialID = material.getMaterialID();
        this.materialName = material.getMaterialName();
    }
}