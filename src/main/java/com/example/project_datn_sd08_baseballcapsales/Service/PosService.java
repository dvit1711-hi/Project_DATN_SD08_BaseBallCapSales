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

    PosOrderGetDto getOrder(Integer orderId);

    PosOrderGetDto updateOrderInfo(Integer orderId, PutOfflineOrderInfoDto dto);

    PosOrderGetDto addItem(Integer orderId, PostOfflineOrderItemDto dto);

    PosOrderGetDto updateItem(Integer orderId, Integer orderDetailId, PutOfflineOrderItemDto dto);

    PosOrderGetDto removeItem(Integer orderId, Integer orderDetailId);

    PosOrderGetDto applyCoupon(Integer orderId, PostApplyCouponDto dto);

    PosOrderGetDto checkout(Integer orderId, PostCheckoutOrderDto dto);

    void cancelPendingOrder(Integer orderId, String email);
}