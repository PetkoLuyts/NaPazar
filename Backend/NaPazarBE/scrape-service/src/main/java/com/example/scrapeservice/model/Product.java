package com.example.scrapeservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id", nullable = false)
    private Promotion promotion;

    @Column(name = "title")
    private String title;

    @Column(name = "old_price")
    private Double oldPrice;

    @Column(name = "new_price")
    private Double newPrice;

    @Column(name = "discount_phrase")
    private String discountPhrase;
}
