package com.example.scrapeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PromotionPeriod {
    private Date startDate;
    private Date endDate;
}
