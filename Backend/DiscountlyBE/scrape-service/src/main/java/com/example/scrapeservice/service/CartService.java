package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.CartItemResponse;

import java.util.List;

public interface CartService {

    void addItemToCart(Integer productId);
    List<CartItemResponse> getCart();
    void updateItemQuantity(Integer productId, Integer quantity);
    void removeItemFromCart(Integer productId);
}
