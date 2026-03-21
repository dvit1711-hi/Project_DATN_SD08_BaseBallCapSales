package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Review;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get all reviews
    public List<GetReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(GetReviewDto::new)
                .toList();
    }

    // Get reviews by product
    public List<GetReviewDto> getReviewsByProductId(Integer productId) {
        return reviewRepository.findAll()
                .stream()
                .filter(r -> r.getProduct().getId().equals(productId))
                .map(GetReviewDto::new)
                .toList();
    }

    // Get reviews by account
    public List<GetReviewDto> getReviewsByAccountId(Integer accountId) {
        return reviewRepository.findAll()
                .stream()
                .filter(r -> r.getAccount().getId().equals(accountId))
                .map(GetReviewDto::new)
                .toList();
    }

    // Get review by id
    public GetReviewDto getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đánh giá không tìm thấy"));
        return new GetReviewDto(review);
    }

    // Create new review
    public GetReviewDto createReview(PostReviewDto dto) {
        // Check if product exists
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

        // Check if account exists
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tìm thấy"));

        Review review = new Review();
        review.setProduct(product);
        review.setAccount(account);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(Instant.now());

        Review savedReview = reviewRepository.save(review);
        return new GetReviewDto(savedReview);
    }

    // Update review
    public GetReviewDto updateReview(Integer id, PutReviewDto dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đánh giá không tìm thấy"));

        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }
        if (dto.getComment() != null) {
            review.setComment(dto.getComment());
        }

        Review updatedReview = reviewRepository.save(review);
        return new GetReviewDto(updatedReview);
    }

    // Delete review
    public boolean deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Đánh giá không tìm thấy");
        }
        reviewRepository.deleteById(id);
        return true;
    }

    // Get average rating for product
    public Double getAverageRatingForProduct(Integer productId) {
        List<GetReviewDto> reviews = getReviewsByProductId(productId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(GetReviewDto::getRating)
                .average()
                .orElse(0.0);
    }

    // Get total reviews count for product
    public Integer getTotalReviewsForProduct(Integer productId) {
        return getReviewsByProductId(productId).size();
    }
}
