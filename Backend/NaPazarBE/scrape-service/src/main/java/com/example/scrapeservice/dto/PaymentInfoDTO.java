package com.example.scrapeservice.dto;

public record PaymentInfoDTO(double amount,
                             String currency,
                             String receiptEmail) {
}
