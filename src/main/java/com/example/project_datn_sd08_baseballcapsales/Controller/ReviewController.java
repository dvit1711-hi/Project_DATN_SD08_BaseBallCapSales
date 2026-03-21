package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Get all reviews
    @GetMapping
    public ResponseEntity<List<GetReviewDto>> getAllReviews() {
        List<GetReviewDto> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // Get review by id
    @GetMapping("/{id}")
    public ResponseEntity<GetReviewDto> getReviewById(@PathVariable Integer id) {
        GetReviewDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    // Get reviews by product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<GetReviewDto>> getReviewsByProductId(@PathVariable Integer productId) {
        List<GetReviewDto> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // Get reviews by account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<GetReviewDto>> getReviewsByAccountId(@PathVariable Integer accountId) {
        List<GetReviewDto> reviews = reviewService.getReviewsByAccountId(accountId);
        return ResponseEntity.ok(reviews);
    }

    // Get average rating for product
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForProduct(@PathVariable Integer productId) {
        Double averageRating = reviewService.getAverageRatingForProduct(productId);
        return ResponseEntity.ok(averageRating);
    }

    // Get total reviews count for product
    @GetMapping("/product/{productId}/total-count")
    public ResponseEntity<Integer> getTotalReviewsForProduct(@PathVariable Integer productId) {
        Integer totalCount = reviewService.getTotalReviewsForProduct(productId);
        return ResponseEntity.ok(totalCount);
    }

    // Create new review
    @PostMapping
    public ResponseEntity<GetReviewDto> createReview(@RequestBody PostReviewDto dto) {
        GetReviewDto review = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    // Update review
    @PutMapping("/{id}")
    public ResponseEntity<GetReviewDto> updateReview(@PathVariable Integer id, @RequestBody PutReviewDto dto) {
        GetReviewDto review = reviewService.updateReview(id, dto);
        return ResponseEntity.ok(review);
    }

    // Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Đánh giá đã được xóa thành công");
    }
}
