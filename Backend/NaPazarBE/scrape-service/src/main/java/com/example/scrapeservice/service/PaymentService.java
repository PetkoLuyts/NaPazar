package com.example.scrapeservice.service;

import com.example.scrapeservice.dto.PaymentInfoDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface PaymentService {

    PaymentIntent createPaymentIntent(PaymentInfoDTO paymentInfoDTO) throws StripeException;
}
