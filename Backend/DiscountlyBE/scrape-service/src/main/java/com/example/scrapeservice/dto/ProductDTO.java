package com.example.scrapeservice.dto;

public record ProductDTO(int id,
                         String title,
                         Double oldPrice,
                         Double newPrice,
                         String discountPhrase) {
}
