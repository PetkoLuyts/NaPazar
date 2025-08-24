package com.example.scrapeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PromotionInterval {
    private Date promotionStarts;
    private Date promotionExpires;
}
