package com.stockflow_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ProductResponseDto {

    private String name;
    private String barcode;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private Boolean active;
    private String categoryName;


}
