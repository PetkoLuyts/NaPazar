package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.exceptions.ProductException;
import com.example.scrapeservice.exceptions.UserException;
import com.example.scrapeservice.model.AppUser;
import com.example.scrapeservice.model.Cart;
import com.example.scrapeservice.model.CartItem;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.CartRepository;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.repository.UserRepository;
import com.example.scrapeservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    public void addItemToCart(Integer productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException("Product with id " + productId + " not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder().user(user).build());

        // TODO: Remove hardcoded quantity once frontend impl is done
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .productId(product.getId())
                .price(product.getNewPrice())
                .quantity(1)
                .build();

        cart.getItems().add(cartItem);

        cartRepository.save(cart);
    }
}
