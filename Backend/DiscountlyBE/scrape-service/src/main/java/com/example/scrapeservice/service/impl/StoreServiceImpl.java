package com.example.scrapeservice.service.impl;

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
}
