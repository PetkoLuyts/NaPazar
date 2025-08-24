package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.dto.PaginatedProductResponse;
import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.mapper.ProductDTOMapper;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductDTOMapper productDTOMapper;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public PaginatedProductResponse getAllProducts(String searchTerm, String storeIds, PageRequest pageRequest) {
        List<Integer> storeIdList = storeIds != null && !storeIds.isEmpty() ?
                Arrays.stream(storeIds.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList()
                : List.of();

        Page<Product> pageResult = productRepository.findAllWithFilters(
                searchTerm,
                storeIdList.isEmpty() ? null : storeIdList,
                pageRequest
        );

        List<ProductDTO> products = pageResult.getContent().stream()
                .map(productDTOMapper)
                .toList();

        return new PaginatedProductResponse(
                products,
                pageResult.getTotalPages(),
                pageResult.getTotalElements()
        );
    }
}
