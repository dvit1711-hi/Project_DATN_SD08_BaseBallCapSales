package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Brand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBranDto {
    private Integer brandID;
    @Size(max = 100)
    @NotNull
    @Nationalized
    private String name;

    public GetBranDto(Brand brand) {
        this.brandID = brand.getBrandID();
        this.name = brand.getName();
    }
}
