package com.example.scrapeservice.dto;

public record CartItemResponse(int id,
                               int productId,
                               String productTitle,
                               double price,
                               int quantity) {
}
