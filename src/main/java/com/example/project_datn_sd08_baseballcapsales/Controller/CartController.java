package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartItemDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Service.CartItemService;
import com.example.project_datn_sd08_baseballcapsales.Service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public List<GetCartDto> getAll() {
        return cartService.getAll();
    }

    @PostMapping
    public ResponseEntity<GetCartDto> create(@Valid @RequestBody PostCartDto dto) {
        Cart created = cartService.create(dto);
        return ResponseEntity.ok(new GetCartDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCartDto> update(@PathVariable Integer id,
                                             @Valid @RequestBody PutCartDto dto) {
        Cart updated = cartService.update(id, dto);
        return ResponseEntity.ok(new GetCartDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy giỏ hàng theo accountId
     * GET /api/carts/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<GetCartDto> getCartByAccountId(@PathVariable Integer accountId) {
        Cart cart = cartService.getCartByAccountId(accountId);
        return ResponseEntity.ok(new GetCartDto(cart));
    }

    /**
     * Lấy tất cả cart items cho một giỏ hàng
     * GET /api/carts/{cartId}/items
     */
    @GetMapping("/{cartId}/items")
    public ResponseEntity<List<GetCartItemDto>> getCartItems(@PathVariable Integer cartId) {
        List<GetCartItemDto> items = cartItemService.getCartItems(cartId);
        return ResponseEntity.ok(items);
    }

    /**
     * Xóa tất cả items trong giỏ hàng
     * DELETE /api/carts/{cartId}/clear
     */
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable Integer cartId) {
        cartService.clearCart(cartId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Giỏ hàng đã được xóa");
        return ResponseEntity.ok(response);
    }
}
