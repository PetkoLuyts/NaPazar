package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.dto.CartItemResponse;
import com.example.scrapeservice.exceptions.CartException;
import com.example.scrapeservice.exceptions.ProductException;
import com.example.scrapeservice.exceptions.UserException;
import com.example.scrapeservice.mapper.CartItemDTOMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemDTOMapper cartItemDTOMapper;

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

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        cart.getItems().add(cartItem);

        cartRepository.save(cart);
    }

    @Override
    public List<CartItemResponse> getCart() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found"));

        return cart.getItems().stream()
                .map(cartItemDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public void updateItemQuantity(Integer productId, Integer quantity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartException("Product not found in cart"));

        if (quantity <= 0) {
            throw new CartException("Quantity must be greater than 0");
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Integer productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartException("Product not found in cart"));

        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }
}
