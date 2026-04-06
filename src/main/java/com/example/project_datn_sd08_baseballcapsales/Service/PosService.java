package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderInfoDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutOfflineOrderItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.*;

import java.util.List;

public interface PosService {

    List<PosProductColorGetDto> searchProducts(String keyword);

    List<PosCustomerGetDto> searchCustomers(String keyword);

    List<PosOrderGetDto> getPendingOrders(String email);

    PosOrderGetDto createOfflineOrder(PostOfflineOrderDto dto, String email);

    PosOrderGetDto getOrder(Integer orderId, String email);

    PosOrderGetDto updateOrderInfo(Integer orderId, PutOfflineOrderInfoDto dto, String email);

    PosOrderGetDto addItem(Integer orderId, PostOfflineOrderItemDto dto, String email);

    PosOrderGetDto updateItem(Integer orderId, Integer orderDetailId, PutOfflineOrderItemDto dto, String email);

    PosOrderGetDto removeItem(Integer orderId, Integer orderDetailId, String email);

    PosOrderGetDto applyCoupon(Integer orderId, PostApplyCouponDto dto, String email);

    List<PosPromotionGetDto> getAvailablePromotions(Integer orderId, String email);

    PosOrderGetDto checkout(Integer orderId, PostCheckoutOrderDto dto, String email);

    void cancelPendingOrder(Integer orderId, String email);
}