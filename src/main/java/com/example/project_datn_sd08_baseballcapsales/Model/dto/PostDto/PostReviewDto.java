package com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReviewDto {
    @NotNull(message = "ID sản phẩm không được trống")
    private Integer productId;

    @NotNull(message = "ID tài khoản không được trống")
    private Integer accountId;

    @NotNull(message = "Đánh giá không được trống")
    @Min(value = 1, message = "Đánh giá phải từ 1 đến 5 sao")
    @Max(value = 5, message = "Đánh giá phải từ 1 đến 5 sao")
    private Integer rating;

    @Size(max = 500, message = "Bình luận không được vượt quá 500 ký tự")
    private String comment;
}
