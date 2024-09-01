package com.example.scrapeservice.controller;

import com.example.scrapeservice.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/cart")
@RestController
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-item/{id}")
    public void addItemToCart(@PathVariable("id") Integer productId) {
        cartService.addItemToCart(productId);
    }
}
