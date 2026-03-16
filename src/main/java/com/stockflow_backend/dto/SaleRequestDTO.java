package com.stockflow_backend.dto;

import com.stockflow_backend.entities.PaymentMethod;
import com.stockflow_backend.entities.SaleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SaleRequestDTO {

    @NotNull
    private SaleStatus status;

    @NotNull
    private PaymentMethod paymentMethod;

    private BigDecimal amountPaid;

}