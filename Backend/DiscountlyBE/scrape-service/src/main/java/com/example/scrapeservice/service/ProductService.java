package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.model.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);

    List<ProductDTO> getAllProducts();
}
