package com.stockflow_backend.dto.response;

import com.stockflow_backend.entities.PaymentMethod;
import com.stockflow_backend.entities.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter @Getter
public class SaleResponseDto {

    private LocalDateTime saleDate;
    private SaleStatus status;
    private BigDecimal total = BigDecimal.ZERO;
    private PaymentMethod paymentMethod;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;

}
