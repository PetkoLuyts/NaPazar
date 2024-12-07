package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.exceptions.ProductException;
import com.example.scrapeservice.model.AppUser;
import com.example.scrapeservice.model.FavouriteItem;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.FavouriteItemRepository;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.repository.UserRepository;
import com.example.scrapeservice.service.FavouriteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FavouriteItemServiceImpl implements FavouriteItemService {

    private final FavouriteItemRepository favouriteItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addFavouriteItem(int id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product does not exist"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        FavouriteItem favouriteItem = FavouriteItem.builder()
                .user(user)
                .product(product)
                .build();

        favouriteItemRepository.save(favouriteItem);
    }

    @Override
    public void removeFavouriteItem(int id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        FavouriteItem favouriteItem = favouriteItemRepository.findByUserAndProductId(user.getId(), id)
                .orElseThrow(() -> new IllegalStateException("Favorite item not found"));

        favouriteItemRepository.delete(favouriteItem);
    }

}
