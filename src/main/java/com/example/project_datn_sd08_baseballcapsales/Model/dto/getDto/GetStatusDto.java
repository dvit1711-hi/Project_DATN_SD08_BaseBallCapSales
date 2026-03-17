package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetStatusDto {
    private Integer id;
    private String statusName;

    public GetStatusDto(Status status) {
        this.id = status.getId();
        this.statusName = status.getStatusName();
    }
}