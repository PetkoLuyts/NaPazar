package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.repository.PaymentRepository;
import com.example.scrapeservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;

    @Value("${stripe.key.secret}")
    private String stripeKey;
}
