package com.example.scrapeservice.dto;

import java.util.List;

public record PaginatedProductResponse(List<ProductDTO> products, int totalPages, long totalElements) {
}
