package com.example.project_datn_sd08_baseballcapsales.Controller;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public List<GetCartDto> getAll() {
        return cartService.getAll();
    }

    @PostMapping
    public ResponseEntity<Cart> create(@Valid @RequestBody PostCartDto dto) {
        return ResponseEntity.ok(cartService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cart> update(@PathVariable Integer id,
                                       @Valid @RequestBody PutCartDto dto) {
        return ResponseEntity.ok(cartService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        cartService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
