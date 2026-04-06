package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetReviewDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetPaidOrderWithDetailsDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.*;
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
                .filter(r -> r.getProductID().getId().equals(productId))
                .map(GetReviewDto::new)
                .toList();
    }

    // Get reviews by account
    public List<GetReviewDto> getReviewsByAccountId(Integer accountId) {
        return reviewRepository.findAll()
                .stream()
                .filter(r -> r.getAccountID().getId().equals(accountId))
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

        // Check if the product was purchased in a paid order
        boolean productPurchased = verifyProductPurchasedInPaidOrder(dto.getAccountId(), dto.getProductId());
        if (!productPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm từ các đơn hàng đã thanh toán");
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

    // Get paid orders with details for account
    public List<GetPaidOrderWithDetailsDto> getPaidOrdersWithDetailsForAccount(Integer accountId) {
        List<Order> paidOrders = orderRepository.findPaidOrdersByAccountId(accountId);
        return paidOrders.stream().map(order -> {
            var payment = paymentRepository.findTopByOrderIDOrderByIdDesc(order);
            String paymentStatus = payment.map(Payment::getStatus).orElse("UNKNOWN");
            String paymentMethod = payment.map(Payment::getMethod).orElse("UNKNOWN");
            var orderDetails = orderDetailRepository.findByOrderID_Id(order.getId());
            return new GetPaidOrderWithDetailsDto(order, paymentStatus, paymentMethod, orderDetails);
        }).toList();
    }

    // Verify if product was purchased in a paid order
    private boolean verifyProductPurchasedInPaidOrder(Integer accountId, Integer productId) {
        List<Order> paidOrders = orderRepository.findPaidOrdersByAccountId(accountId);

        for (Order order : paidOrders) {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderID_Id(order.getId());

            for (OrderDetail detail : orderDetails) {
                if (detail.getProductColorID().getProductID().getId().equals(productId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
