package com.stockflow_backend.mapper;

import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.dto.response.ProductResponseDto;
import com.stockflow_backend.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductRequestDTO productRequestDTO);
    ProductResponseDto toProductResponseDto(Product product);

}
