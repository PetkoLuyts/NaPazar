package com.example.scrapeservice.dto;

import lombok.Builder;

@Builder
public record ProductDTO(int id,
                         String title,
                         Double oldPrice,
                         Double newPrice,
                         String discountPhrase) {
}
