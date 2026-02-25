package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.CartItem;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Product;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartItemRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.ProductRepository;
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
    private ProductRepository productRepository;

    public List<GetCartItemDto> getAll() {
        return cartItemRepository.findAll()
                .stream()
                .map(GetCartItemDto::new)
                .toList();
    }

    public CartItem create(PostCartItemDto dto) {
        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(dto.getProductID())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = new CartItem();
        item.setId(dto.getCartItemID());
        item.setCartID(cart);
        item.setProductID(product);
        item.setQuantity(dto.getQuantity());

        return cartItemRepository.save(item);
    }

    public CartItem update(Integer id, PutCartItemDto dto) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        Cart cart = cartRepository.findById(dto.getCartID())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(dto.getProductID())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        item.setCartID(cart);
        item.setProductID(product);
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
