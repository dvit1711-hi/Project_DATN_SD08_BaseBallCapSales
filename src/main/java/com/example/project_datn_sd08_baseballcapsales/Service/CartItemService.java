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

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("ProductColor not found"));

        CartItem item = new CartItem();
        item.setCartID(cart);
        item.setProductColorID(productColor);
        item.setQuantity(dto.getQuantity());

        return cartItemRepository.save(item);
    }

    public CartItem update(Integer id, PutCartItemDto dto) {

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        ProductColor productColor = productColorRepository.findById(dto.getProductColorID())
                .orElseThrow(() -> new RuntimeException("ProductColor not found"));

        item.setCartID(cart);
        item.setProductColorID(productColor);
        item.setQuantity(dto.getQuantity());

        return cartItemRepository.save(item);
    }

    public boolean delete(Integer id) {
        if (!cartItemRepository.existsById(id)) {
            throw new RuntimeException("Cart item not found");
        }
        cartItemRepository.deleteById(id);
        return true;
    }
}
