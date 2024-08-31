package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE " +
            "(:searchTerm IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
            "(:storeIds IS NULL OR p.promotion.storeByStoreId.id IN :storeIds)")
    Page<Product> findAllWithFilters(@Param("searchTerm") String searchTerm,
                                     @Param("storeIds") List<Integer> storeIds,
                                     Pageable pageable);
}
