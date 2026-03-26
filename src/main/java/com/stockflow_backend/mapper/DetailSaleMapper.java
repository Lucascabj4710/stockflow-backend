package com.stockflow_backend.mapper;

import com.stockflow_backend.dto.request.DetailSaleRequestDTO;
import com.stockflow_backend.entities.DetailSale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetailSaleMapper {

    DetailSale toDetailSale(DetailSaleRequestDTO detailSaleRequestDTO);

}
