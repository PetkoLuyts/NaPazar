package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/product")
@RestController
public class ProductController {

    private final ProductService productService;

    @GetMapping ("/all")
    public List<ProductDTO> getAllProducts(@RequestParam(required = false) String searchTerm,
                                           @RequestParam(required = false) String storeIds) {
        return productService.getAllProducts(searchTerm, storeIds);
    }
}
