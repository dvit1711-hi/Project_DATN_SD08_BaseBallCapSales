package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.*;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.CartItem;
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
    public ResponseEntity<CartItem> create(@Valid @RequestBody PostCartItemDto dto) {
        return ResponseEntity.ok(cartItemService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> update(@PathVariable Integer id,
                                           @Valid @RequestBody PutCartItemDto dto) {
        return ResponseEntity.ok(cartItemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        cartItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
