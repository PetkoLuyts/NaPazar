package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.mapper.ProductDTOMapper;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<ProductDTO> getAllProducts(String searchTerm, String storeIds) {
        List<Integer> storeIdList = storeIds != null ?
                Arrays.stream(storeIds.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt).toList()
                : List.of();

        return productRepository.findAll()
                .stream()
                .filter(product ->
                        (searchTerm == null || product.getTitle().toLowerCase().contains(searchTerm.toLowerCase())) &&
                                (storeIdList.isEmpty() || storeIdList.contains(product.getPromotion().getStoreByStoreId().getId()))
                )
                .map(productDTOMapper)
                .collect(Collectors.toList());
    }
}
