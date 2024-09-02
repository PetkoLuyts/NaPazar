package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.CartItemResponse;
import com.example.scrapeservice.model.Cart;
import com.example.scrapeservice.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/cart")
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-item/{id}")
    public void addItemToCart(@PathVariable("id") Integer productId) {
        cartService.addItemToCart(productId);
    }

    @GetMapping("/items")
    public List<CartItemResponse> getCartItems() {
        return cartService.getCart();
    }
}
