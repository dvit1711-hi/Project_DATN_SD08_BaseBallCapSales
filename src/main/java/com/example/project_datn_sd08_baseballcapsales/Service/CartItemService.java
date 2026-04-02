package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.CartItem;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartItemRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartRepository;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import com.example.project_datn_sd08_baseballcapsales.Service.ProductDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductColorRepository productColorRepository;

    @Autowired
    private ProductDiscountService productDiscountService;

    public List<GetCartItemDto> getAll() {
        return cartItemRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CartItem create(PostCartItemDto dto) {
        if (dto == null || dto.getCartID() == null || dto.getProductColorID() == null) {
            throw new RuntimeException("Thiếu cartID hoặc productColorID");
        }

        int addQuantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        if (addQuantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm màu"));

        // Kiểm tra tồn kho
        if (productColor.getStockQuantity() == null || productColor.getStockQuantity() < addQuantity) {
            throw new RuntimeException("Số lượng tồn kho không đủ. Tồn kho hiện tại: "
                    + (productColor.getStockQuantity() != null ? productColor.getStockQuantity() : 0));
        }

        List<CartItem> duplicatedItems = cartItemRepository
                .findByCartID_IdAndProductColorID_Id(cart.getId(), productColor.getId());

        if (!duplicatedItems.isEmpty()) {
            CartItem primaryItem = duplicatedItems.get(0);
            int currentQty = primaryItem.getQuantity() == null ? 0 : primaryItem.getQuantity();

            for (int i = 1; i < duplicatedItems.size(); i++) {
                CartItem duplicate = duplicatedItems.get(i);
                currentQty += duplicate.getQuantity() == null ? 0 : duplicate.getQuantity();
            }

            int newTotal = currentQty + addQuantity;

            // Kiểm tra tổng tồn kho
            if (productColor.getStockQuantity() < newTotal) {
                throw new RuntimeException("Tổng số lượng vượt quá tồn kho. Tồn kho: "
                        + productColor.getStockQuantity() + ", yêu cầu: " + newTotal);
            }

            primaryItem.setQuantity(newTotal);
            CartItem saved = cartItemRepository.save(primaryItem);

            if (duplicatedItems.size() > 1) {
                List<CartItem> redundantItems = duplicatedItems.subList(1, duplicatedItems.size());
                cartItemRepository.deleteAll(redundantItems);
            }
            return saved;
        }

        CartItem item = new CartItem();
        item.setCartID(cart);
        item.setProductColorID(productColor);
        item.setQuantity(addQuantity);

        return cartItemRepository.save(item);
    }

    public CartItem update(Integer id, PutCartItemDto dto) {
        int nextQuantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        if (nextQuantity <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm màu"));

        // Kiểm tra tồn kho
        if (productColor.getStockQuantity() == null || productColor.getStockQuantity() < nextQuantity) {
            throw new RuntimeException("Số lượng tồn kho không đủ. Tồn kho hiện tại: "
                    + (productColor.getStockQuantity() != null ? productColor.getStockQuantity() : 0));
        }

        List<CartItem> duplicatedItems = new ArrayList<>(
                cartItemRepository.findByCartID_IdAndProductColorID_Id(cart.getId(), productColor.getId())
        );

        // Keep exactly one row per cart + product color by merging into the current item.
        int mergedQuantity = nextQuantity;
        for (CartItem duplicated : duplicatedItems) {
            if (!duplicated.getId().equals(item.getId())) {
                mergedQuantity += duplicated.getQuantity() == null ? 0 : duplicated.getQuantity();
            }
        }

        // Kiểm tra tồn kho cho tổng sau merge
        if (productColor.getStockQuantity() < mergedQuantity) {
            throw new RuntimeException("Tổng số lượng vượt quá tồn kho. Tồn kho: "
                    + productColor.getStockQuantity() + ", yêu cầu: " + mergedQuantity);
        }

        item.setCartID(cart);
        item.setProductColorID(productColor);
        item.setQuantity(mergedQuantity);

        CartItem saved = cartItemRepository.save(item);

        duplicatedItems.removeIf(duplicated -> duplicated.getId().equals(item.getId()));
        if (!duplicatedItems.isEmpty()) {
            cartItemRepository.deleteAll(duplicatedItems);
        }

        return saved;
    }

    public boolean delete(Integer id) {
        if (!cartItemRepository.existsById(id)) {
            throw new RuntimeException("Cart item not found");
        }
        cartItemRepository.deleteById(id);
        return true;
    }

    /**
     * Lấy tất cả items trong một giỏ hàng
     */
    public List<GetCartItemDto> getCartItems(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        return cartItemRepository.findByCartID_Id(cartId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private GetCartItemDto toDto(CartItem item) {
        GetCartItemDto dto = new GetCartItemDto(item);
        if (item.getProductColorID() != null) {
            BigDecimal discountedPrice = productDiscountService.getDiscountedPrice(item.getProductColorID());
            dto.setPrice(discountedPrice == null ? 0L : discountedPrice.longValue());
        }
        return dto;
    }
}
