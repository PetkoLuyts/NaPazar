package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.constants.Constants;
import com.example.scrapeservice.dto.PaginatedProductResponse;
import com.example.scrapeservice.dto.ProductDTO;
import com.example.scrapeservice.exceptions.ProductException;
import com.example.scrapeservice.model.AppUser;
import com.example.scrapeservice.model.FavouriteItem;
import com.example.scrapeservice.model.Product;
import com.example.scrapeservice.repository.FavouriteItemRepository;
import com.example.scrapeservice.repository.ProductRepository;
import com.example.scrapeservice.repository.UserRepository;
import com.example.scrapeservice.service.FavouriteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() -> new IllegalStateException(Constants.USER_NOT_FOUND));

        FavouriteItem favouriteItem = FavouriteItem.builder()
                .user(user)
                .product(product)
                .build();

        favouriteItemRepository.save(favouriteItem);
    }

    @Override
    public PaginatedProductResponse getAllFavouriteItems(PageRequest pageRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException(Constants.USER_NOT_FOUND));

        Page<FavouriteItem> pageResult = favouriteItemRepository.findAllByUser(user.getId(), pageRequest);

        List<ProductDTO> products = pageResult.getContent().stream()
                .map(favouriteItem -> {
                    Product product = favouriteItem.getProduct();
                    return ProductDTO.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .oldPrice(product.getOldPrice())
                            .newPrice(product.getNewPrice())
                            .discountPhrase(product.getDiscountPhrase())
                            .build();
                })
                .toList();

        return new PaginatedProductResponse(
                products,
                pageResult.getTotalPages(),
                pageResult.getTotalElements()
        );
    }

    @Override
    public void removeFavouriteItem(int id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException(Constants.USER_NOT_FOUND));

        FavouriteItem favouriteItem = favouriteItemRepository.findByUserAndProductId(user.getId(), id)
                .orElseThrow(() -> new IllegalStateException("Favorite item not found"));

        favouriteItemRepository.delete(favouriteItem);
    }

}
