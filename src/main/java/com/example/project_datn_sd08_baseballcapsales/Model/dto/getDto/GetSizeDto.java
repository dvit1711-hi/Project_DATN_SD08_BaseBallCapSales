package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.SizeEntity;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSizeDto {

    private Integer sizeID;
    private String sizeName;
    private String sizeDescription;
    @Size(max = 20)
    private String status;

    public GetSizeDto(SizeEntity size) {
        this.sizeID = size.getSizeID();
        this.sizeName = size.getSizeName();
        this.sizeDescription = size.getSizeDescription();
        this.status = size.getStatus();
    }
}