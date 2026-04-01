package com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PutProductDto {

    @Size(max = 200)
    @Nationalized
    private String productName;

    @Size(max = 500)
    @Nationalized
    private String description;

    @Size(max = 20)
    private String status;

    @Size(max = 100)
    @NotNull
    @Nationalized
    private String brandName;

    private Integer brandID;

    private Integer materialID;
}