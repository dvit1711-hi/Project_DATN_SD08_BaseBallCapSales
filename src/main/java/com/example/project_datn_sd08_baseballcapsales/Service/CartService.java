package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<GetCartDto> getAll() {
        return cartRepository.findAll()
                .stream()
                .map(GetCartDto::new)
                .toList();
    }

    public Cart create(PostCartDto dto) {
        Account acc = accountRepository.findById(dto.getAccountID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        Cart cart = new Cart();
        cart.setId(dto.getCartID());
        cart.setAccountID(acc);

        return cartRepository.save(cart);
    }

    public Cart update(Integer id, PutCartDto dto) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        Account acc = accountRepository.findById(dto.getAccountID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        cart.setAccountID(acc);

        return cartRepository.save(cart);
    }

    public boolean delete(Integer id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        cartRepository.deleteById(id);
        return true;
    }
}
