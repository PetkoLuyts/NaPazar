package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.PaginatedProductResponse;
import org.springframework.data.domain.PageRequest;

public interface FavouriteItemService {
    void addFavouriteItem(int id);
    PaginatedProductResponse getAllFavouriteItems(PageRequest pageRequest);
    void removeFavouriteItem(int id);
}
