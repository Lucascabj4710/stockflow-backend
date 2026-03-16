package com.stockflow_backend.mapper;

import com.stockflow_backend.dto.ProductRequestDTO;
import com.stockflow_backend.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductRequestDTO productRequestDTO);

}
