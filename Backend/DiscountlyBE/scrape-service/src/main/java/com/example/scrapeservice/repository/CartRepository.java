package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.AppUser;
import com.example.scrapeservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUser(AppUser user);
}
