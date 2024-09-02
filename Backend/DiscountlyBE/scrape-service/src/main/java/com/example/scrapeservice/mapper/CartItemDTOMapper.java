package com.example.scrapeservice.mapper;

import com.example.scrapeservice.dto.CartItemResponse;
import com.example.scrapeservice.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CartItemDTOMapper implements Function<CartItem, CartItemResponse> {
    @Override
    public CartItemResponse apply(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getProduct().getTitle(),
                cartItem.getPrice(),
                cartItem.getQuantity()
        );
    }
}
