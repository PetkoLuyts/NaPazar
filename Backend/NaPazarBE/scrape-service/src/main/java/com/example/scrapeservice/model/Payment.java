package com.example.scrapeservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "amount")
    private Double amount;
}
