package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.PaginatedProductResponse;
import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/product")
@RestController
public class ProductController {
    private final ProductService productService;

    @GetMapping ("/all")
    public PaginatedProductResponse getAllProducts(@RequestParam(required = false) String searchTerm,
                                                   @RequestParam(required = false) String storeIds,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "8") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return productService.getAllProducts(searchTerm, storeIds, pageRequest);
    }
}
