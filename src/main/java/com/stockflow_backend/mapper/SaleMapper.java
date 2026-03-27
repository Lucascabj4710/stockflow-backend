package com.stockflow_backend.mapper;

import com.stockflow_backend.dto.request.SaleRequestDTO;
import com.stockflow_backend.dto.response.SaleResponseDto;
import com.stockflow_backend.entities.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    Sale toSale(SaleRequestDTO saleRequestDTO);
    SaleResponseDto toSaleResponseDto(Sale sale);

}
