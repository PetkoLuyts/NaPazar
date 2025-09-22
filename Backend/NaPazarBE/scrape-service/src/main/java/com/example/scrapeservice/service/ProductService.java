package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.PaginatedProductResponse;
import com.example.scrapeservice.model.Product;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);

    void createProducts(List<Product> products);

    PaginatedProductResponse getAllProducts(String searchTerm, String storeIds, PageRequest pageRequest);
}
