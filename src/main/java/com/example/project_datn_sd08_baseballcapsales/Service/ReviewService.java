package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
import com.example.project_datn_sd08_baseballcapsales.Model.enums.PaymentStatus;
import com.example.project_datn_sd08_baseballcapsales.Repository.*;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<GetReviewDto> getAllReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(GetReviewDto::new)
                .toList();
    }

    public List<GetReviewDto> getReviewsByProductId(Integer productId) {
        return reviewRepository.findByProductID_Id(productId)
                .stream()
                .map(GetReviewDto::new)
                .toList();
    }

    public List<GetReviewDto> getReviewsByAccountId(Integer accountId) {
        return reviewRepository.findByAccountID_Id(accountId)
                .stream()
                .map(GetReviewDto::new)
                .toList();
    }

    public GetReviewDto getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đánh giá không tìm thấy"));
        return new GetReviewDto(review);
    }

    public GetReviewDto createReview(PostReviewDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tìm thấy"));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tìm thấy"));

        boolean productPurchased = verifyProductPurchasedInPaidOrder(dto.getAccountId(), dto.getProductId());
        if (!productPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm từ các đơn hàng đã thanh toán");
        }

        boolean alreadyReviewed = reviewRepository.existsByAccountID_IdAndProductID_Id(
                dto.getAccountId(),
                dto.getProductId()
        );
        if (alreadyReviewed) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi, mỗi tài khoản chỉ được đánh giá 1 lần");
        }

        Review review = new Review();
        review.setProductID(product);
        review.setAccountID(account);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedAt(Instant.now());

        Review savedReview = reviewRepository.save(review);
        return new GetReviewDto(savedReview);
    }

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

    public boolean deleteReview(Integer id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Đánh giá không tìm thấy");
        }
        reviewRepository.deleteById(id);
        return true;
    }

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

    public Integer getTotalReviewsForProduct(Integer productId) {
        return getReviewsByProductId(productId).size();
    }

    public List<GetPaidOrderWithDetailsDto> getPaidOrdersWithDetailsForAccount(Integer accountId) {
        List<Order> paidOrders = orderRepository.findPaidOrdersByAccountId(accountId);

        return paidOrders.stream().map(order -> {

            var paymentOpt = paymentRepository.findTopByOrderIDOrderByIdDesc(order);

            PaymentStatus paymentStatus = paymentOpt
                    .map(Payment::getStatus)
                    .orElse(PaymentStatus.UNKNOWN);

            String paymentMethod = paymentOpt
                    .map(Payment::getMethod)
                    .orElse("UNKNOWN");

            var orderDetails = orderDetailRepository.findByOrderID_Id(order.getId());

            return new GetPaidOrderWithDetailsDto(
                    order,
                    paymentStatus,   // ✅ enum
                    paymentMethod,
                    orderDetails
            );
        }).toList();
    }

    private boolean verifyProductPurchasedInPaidOrder(Integer accountId, Integer productId) {
        return orderDetailRepository.existsPurchasedProductForAccount(accountId, productId);
    }
}