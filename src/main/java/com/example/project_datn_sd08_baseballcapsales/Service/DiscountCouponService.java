package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetDiscountCouponDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.DiscountCoupon;
import com.example.project_datn_sd08_baseballcapsales.Repository.DiscountCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountCouponService {

    @Autowired
    private DiscountCouponRepository discountCouponRepository;

    public List<GetDiscountCouponDto> getAllCoupons() {
        return discountCouponRepository.findAll()
                .stream()
                .map(GetDiscountCouponDto::new)
                .toList();
    }

    public DiscountCoupon postdiscount(PostDiscountCouponDto dto) {
        DiscountCoupon discountCoupon = new DiscountCoupon();
        discountCoupon.setCouponCode(dto.getCouponCode());
        discountCoupon.setName(dto.getName());
        discountCoupon.setDiscountType(dto.getDiscountType());
        discountCoupon.setDiscountValue(dto.getDiscountValue());
        discountCoupon.setMinOrderValue(dto.getMinOrderValue());
        discountCoupon.setMaxDiscountValue(dto.getMaxDiscountValue());
        discountCoupon.setQuantity(dto.getQuantity());
        discountCoupon.setStartDate(dto.getStartDate());
        discountCoupon.setEndDate(dto.getEndDate());
        discountCoupon.setActive(dto.getActive() != null ? dto.getActive() : true);
        discountCoupon.setDescription(dto.getDescription());
        discountCoupon.setStatus("ACTIVE");

        return discountCouponRepository.save(discountCoupon);
    }

    public DiscountCoupon putCoupon(Integer id, PutDiscountCouponDto dto) {
        DiscountCoupon discountCoupon = discountCouponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon không tìm thấy"));

        if (dto.getCouponCode() != null)
            discountCoupon.setCouponCode(dto.getCouponCode());
        if (dto.getName() != null)
            discountCoupon.setName(dto.getName());
        if (dto.getDiscountType() != null)
            discountCoupon.setDiscountType(dto.getDiscountType());
        if (dto.getDiscountValue() != null)
            discountCoupon.setDiscountValue(dto.getDiscountValue());
        if (dto.getMinOrderValue() != null)
            discountCoupon.setMinOrderValue(dto.getMinOrderValue());
        if (dto.getMaxDiscountValue() != null)
            discountCoupon.setMaxDiscountValue(dto.getMaxDiscountValue());
        if (dto.getQuantity() != null)
            discountCoupon.setQuantity(dto.getQuantity());
        if (dto.getStartDate() != null)
            discountCoupon.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            discountCoupon.setEndDate(dto.getEndDate());
        if (dto.getActive() != null)
            discountCoupon.setActive(dto.getActive());
        if (dto.getDescription() != null)
            discountCoupon.setDescription(dto.getDescription());

        return discountCouponRepository.save(discountCoupon);
    }

    public boolean delete(Integer id) {
        if (!discountCouponRepository.existsById(id)) {
            throw new RuntimeException("discountCoupon not found");
        }
        discountCouponRepository.deleteById(id);
        return true;
    }

    public GetDiscountCouponDto getCouponById(Integer id) {
        return discountCouponRepository.findById(id)
                .map(GetDiscountCouponDto::new)
                .orElseThrow(() -> new RuntimeException("discountCoupon not found"));
    }

    public List<GetDiscountCouponDto> getCouponsByStatus(String status) {
        return discountCouponRepository.findAll()
                .stream()
                .filter(c -> c.getStatus() != null && c.getStatus().equals(status))
                .map(GetDiscountCouponDto::new)
                .toList();
    }

}