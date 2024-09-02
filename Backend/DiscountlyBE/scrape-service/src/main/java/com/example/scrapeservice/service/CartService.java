package com.example.scrapeservice.service;

import com.example.scrapeservice.model.Cart;

public interface CartService {

    void addItemToCart(Integer productId);
    Cart getCart();
}
