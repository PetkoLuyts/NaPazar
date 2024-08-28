package com.example.scrapeservice.repository;

import com.example.scrapeservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByUserEmail(String userEmail);
}
