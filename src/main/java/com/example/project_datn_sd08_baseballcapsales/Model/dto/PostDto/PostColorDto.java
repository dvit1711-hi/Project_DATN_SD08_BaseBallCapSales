package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostColorDto {
    private Integer colorId;       // id của màu (từ Colors table)
    private Integer stockQuantity; // số lượng
}
