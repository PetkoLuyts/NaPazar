package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.model.Promotion;
import com.example.scrapeservice.repository.PromotionRepository;
import com.example.scrapeservice.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
}
