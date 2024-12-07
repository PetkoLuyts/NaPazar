package com.example.scrapeservice.controller;

import com.example.scrapeservice.service.FavouriteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/favourite")
@RestController
public class FavouriteItemController {

    private final FavouriteItemService favouriteItemService;

    @PostMapping("/add/{id}")
    public void addFavouriteItem(@PathVariable("id") int productId) {
        favouriteItemService.addFavouriteItem(productId);
    }

    @DeleteMapping("/remove-item/{id}")
    public void removeFavouriteItem(@PathVariable("id") int productId) {
        favouriteItemService.removeFavouriteItem(productId);
    }
}
