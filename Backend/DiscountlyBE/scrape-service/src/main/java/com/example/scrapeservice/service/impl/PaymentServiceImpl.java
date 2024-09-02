package com.example.scrapeservice.service.impl;

import com.example.scrapeservice.dto.PaymentInfoDTO;
import com.example.scrapeservice.repository.PaymentRepository;
import com.example.scrapeservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;

    @Value("${stripe.key.secret}")
    private String stripeKey;

    public PaymentIntent createPaymentIntent(PaymentInfoDTO paymentInfoDTO) throws StripeException {
        Stripe.apiKey = stripeKey;

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();

        int amountInBGN = (int) Math.round(paymentInfoDTO.amount() * 100);

        params.put("amount", amountInBGN);
        params.put("currency", paymentInfoDTO.currency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }
}
