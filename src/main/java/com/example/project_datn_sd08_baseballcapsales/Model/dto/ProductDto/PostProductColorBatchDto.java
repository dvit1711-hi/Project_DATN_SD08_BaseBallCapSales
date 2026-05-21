package com.example.project_datn_sd08_baseballcapsales.Model.dto.ProductDto;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostProductColorDto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class PostProductColorBatchDto {
    private List<PostProductColorDto> variants;  // danh sách biến thể
}