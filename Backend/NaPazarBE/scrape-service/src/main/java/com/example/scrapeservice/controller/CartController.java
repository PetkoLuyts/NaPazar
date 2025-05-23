package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.CartItemResponse;
import com.example.scrapeservice.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PutMapping("/update-item/{id}")
    public ResponseEntity<Void> updateItemQuantity(@PathVariable("id") Integer productId,
                                                   @RequestParam("quantity") Integer quantity) {
        cartService.updateItemQuantity(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove-item/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable("id") Integer productId) {
        cartService.removeItemFromCart(productId);
        return ResponseEntity.ok().build();
    }
}
