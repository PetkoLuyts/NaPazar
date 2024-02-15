package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoresRepository extends JpaRepository<Stores,Integer> {
}
