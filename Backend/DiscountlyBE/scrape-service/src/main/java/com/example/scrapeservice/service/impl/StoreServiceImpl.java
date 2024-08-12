package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.exceptions.StoreException;
import com.example.scrapeservice.model.Store;
import com.example.scrapeservice.repository.StoreRepository;
import com.example.scrapeservice.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public Store getStoreById(int id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new StoreException("Store with id " + id + " not found"));
    }
}
