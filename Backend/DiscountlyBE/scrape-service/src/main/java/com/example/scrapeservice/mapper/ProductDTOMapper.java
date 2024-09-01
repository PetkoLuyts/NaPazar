package com.example.scrapeservice.mapper;

import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.model.Product;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ProductDTOMapper implements Function<Product, ProductDTO> {
    @Override
    public ProductDTO apply(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getOldPrice(),
                product.getNewPrice(),
                product.getDiscountPhrase()
        );
    }
}
