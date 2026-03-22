package com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto;

import com.example.project_datn_sd08_baseballcapsales.Model.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewDto {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer accountId;
    private String username;
    private Integer rating;
    private String comment;
    private Instant createdAt;

    public GetReviewDto(Review review) {
        this.id = review.getId();
        this.productId = review.getProductID().getId();
        this.productName = review.getProductID().getProductName();
        this.accountId = review.getAccountID().getId();
        this.username = review.getAccountID().getUsername();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }
}
