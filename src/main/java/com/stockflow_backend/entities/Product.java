package com.stockflow_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor @AllArgsConstructor
@Setter @Getter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String barcode;

    private String brand;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean active;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    @Column(precision = 5, scale = 2)
    private BigDecimal discount;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}