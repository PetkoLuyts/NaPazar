package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.FavouriteItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavouriteItemRepository extends JpaRepository<FavouriteItem, Integer> {

    @Query("SELECT f FROM FavouriteItem f WHERE f.user.id = :userId")
    Page<FavouriteItem> findAllByUser(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT f FROM FavouriteItem f WHERE f.user.id = :userId AND f.product.id = :productId")
    Optional<FavouriteItem> findByUserAndProductId(int userId, int productId);
}
