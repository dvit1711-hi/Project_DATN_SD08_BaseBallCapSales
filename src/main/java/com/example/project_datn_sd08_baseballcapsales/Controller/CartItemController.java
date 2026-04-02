package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public List<GetCartItemDto> getAll() {
        return cartItemService.getAll();
    }

    @PostMapping
    public ResponseEntity<GetCartItemDto> create(@Valid @RequestBody PostCartItemDto dto) {
        return ResponseEntity.ok(new GetCartItemDto(cartItemService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCartItemDto> update(@PathVariable Integer id,
                                                 @Valid @RequestBody PutCartItemDto dto) {
        return ResponseEntity.ok(new GetCartItemDto(cartItemService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        cartItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy tất cả items trong một giỏ hàng
     * GET /api/cart-items/cart/{cartId}
     */
    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<GetCartItemDto>> getCartItems(@PathVariable Integer cartId) {
        List<GetCartItemDto> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }
}
