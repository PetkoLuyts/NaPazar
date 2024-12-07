package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.FavouriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteItemRepository extends JpaRepository<FavouriteItem, Integer> {
}
