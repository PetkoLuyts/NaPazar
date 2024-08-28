package com.example.scrapeservice.dto;

public record ProductDTO(String title,
                         Double oldPrice,
                         Double newPrice,
                         String discountPhrase) {
}
