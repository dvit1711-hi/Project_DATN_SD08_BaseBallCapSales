package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.CartItem;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartItemRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartRepository;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.ProductColor;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductColorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<GetCartItemDto> getAll() {
        return cartItemRepository.findAll()
                .stream()
                .map(GetCartItemDto::new)
                .toList();
    }

    public CartItem create(PostCartItemDto dto) {
        int addQuantity = dto.getQuantity() == null ? 1 : dto.getQuantity();
        if (addQuantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("ProductColor not found"));

        List<CartItem> duplicatedItems = cartItemRepository
                .findByCartID_IdAndProductColorID_Id(cart.getId(), productColor.getId());

        if (!duplicatedItems.isEmpty()) {
            CartItem primaryItem = duplicatedItems.get(0);
            int currentQty = primaryItem.getQuantity() == null ? 0 : primaryItem.getQuantity();

            for (int i = 1; i < duplicatedItems.size(); i++) {
                CartItem duplicate = duplicatedItems.get(i);
                currentQty += duplicate.getQuantity() == null ? 0 : duplicate.getQuantity();
            }

            primaryItem.setQuantity(currentQty + addQuantity);
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
            throw new RuntimeException("Quantity must be greater than 0");
        }

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("ProductColor not found"));

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
}
