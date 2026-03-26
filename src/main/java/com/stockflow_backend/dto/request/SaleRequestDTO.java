package com.stockflow_backend.dto.request;

import com.stockflow_backend.entities.PaymentMethod;
import com.stockflow_backend.entities.SaleStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SaleRequestDTO {

    @NotNull
    private PaymentMethod paymentMethod;

    @Min(value = 1, message = "El minimo de monto de pago debe ser mayor a 0")
    private BigDecimal amountPaid;

    @NotEmpty
    List<DetailSaleRequestDTO> detailSaleRequestDTOList;

}