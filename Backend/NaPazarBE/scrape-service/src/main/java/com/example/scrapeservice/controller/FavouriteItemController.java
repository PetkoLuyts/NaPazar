package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.PaginatedProductResponse;
import com.example.scrapeservice.service.FavouriteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/favourite")
@RestController
public class FavouriteItemController {

    private final FavouriteItemService favouriteItemService;

    @PostMapping("/add/{id}")
    public void addFavouriteItem(@PathVariable("id") int productId) {
        favouriteItemService.addFavouriteItem(productId);
    }

    @GetMapping("/all")
    public PaginatedProductResponse getAllFavouriteItems(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "8") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return favouriteItemService.getAllFavouriteItems(pageRequest);
    }

    @DeleteMapping("/remove-item/{id}")
    public void removeFavouriteItem(@PathVariable("id") int productId) {
        favouriteItemService.removeFavouriteItem(productId);
    }
}
