package com.example.scrapeservice.controller;

import com.example.scrapeservice.dto.PaymentInfoDTO;
import com.example.scrapeservice.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/payment")
@RestController
public class PaymentController {
    private PaymentService paymentService;

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoDTO paymentInfoDTO) throws StripeException {
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoDTO);
        String payment = paymentIntent.toJson();

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
