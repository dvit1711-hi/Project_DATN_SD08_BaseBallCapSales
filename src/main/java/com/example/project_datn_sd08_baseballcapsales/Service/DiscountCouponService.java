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
        discountCoupon.setDiscountValue(dto.getDiscountValue());
        discountCoupon.setExpiryDate(dto.getExpiryDate());
        discountCoupon.setStatus(dto.getStatus());

        return discountCouponRepository.save(discountCoupon);
    }

    public DiscountCoupon putCoupon(Integer id, PutDiscountCouponDto dto) {
        DiscountCoupon discountCoupon = discountCouponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("discountCoupon not found"));;
        if (discountCoupon == null) return null;

        discountCoupon.setCouponCode(dto.getCouponCode());
        discountCoupon.setDiscountValue(dto.getDiscountValue());
        discountCoupon.setExpiryDate(dto.getExpiryDate());
        discountCoupon.setStatus(dto.getStatus());

        return discountCouponRepository.save(discountCoupon);
    }

    public boolean delete(Integer id) {
        if (discountCouponRepository.existsById(id)) {
            throw new RuntimeException("discountCoupon not found");
        }
        discountCouponRepository.deleteById(id);
        return true;
    }

}