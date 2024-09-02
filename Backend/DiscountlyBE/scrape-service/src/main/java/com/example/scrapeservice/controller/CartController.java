package com.example.scrapeservice.controller;

import com.example.scrapeservice.model.Cart;
import com.example.scrapeservice.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/cart")
@RestController
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-item/{id}")
    public void addItemToCart(@PathVariable("id") Integer productId) {
        cartService.addItemToCart(productId);
    }

    @GetMapping("/items")
    public Cart getCart() {
        return cartService.getCart();
    }
}
