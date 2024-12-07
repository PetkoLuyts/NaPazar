package com.example.scrapeservice.controller;

import com.example.scrapeservice.service.FavouriteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
