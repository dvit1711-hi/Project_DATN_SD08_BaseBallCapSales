package com.example.project_datn_sd08_baseballcapsales.Service;

import com.example.project_datn_sd08_baseballcapsales.Model.dto.PostDto.PostCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.PutDto.PutCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.dto.getDto.GetCartDto;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Account;
import com.example.project_datn_sd08_baseballcapsales.Model.entity.Cart;
import com.example.project_datn_sd08_baseballcapsales.Repository.AccountRepository;
import com.example.project_datn_sd08_baseballcapsales.Repository.CartItemRepository;
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

    @Autowired
    private CartItemRepository cartItemRepository;

    public List<GetCartDto> getAll() {
        return cartRepository.findAll()
                .stream()
                .map(GetCartDto::new)
                .toList();
    }

    public Cart create(PostCartDto dto) {
        if (dto == null || dto.getAccountID() == null) {
            throw new RuntimeException("Thiếu accountID để tạo giỏ hàng");
        }

        // 1. tìm cart theo account
        Cart existing = cartRepository.findByAccountID_Id(dto.getAccountID())
                .orElse(null);

        // 2. nếu đã có thì trả về luôn
        if (existing != null) {
            return existing;
        }

        // 3. nếu chưa có thì tạo mới
        Account acc = accountRepository.findById(dto.getAccountID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        Cart cart = new Cart();
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

    /**
     * Lấy giỏ hàng của một tài khoản theo accountId
     */
    public Cart getCartByAccountId(Integer accountId) {
        return cartRepository.findByAccountID_Id(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng cho tài khoản này"));
    }

    /**
     * Xóa tất cả items trong giỏ hàng
     */
    public boolean clearCart(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Không tìm thấy giỏ hàng");
        }
        cartItemRepository.deleteAll(cartItemRepository.findByCartID_Id(cartId));
        return true;
    }
}
