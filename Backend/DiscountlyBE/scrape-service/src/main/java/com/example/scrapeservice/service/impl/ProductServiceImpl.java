package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
