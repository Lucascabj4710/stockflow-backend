package com.stockflow_backend.mapper;

import com.stockflow_backend.dto.CategoryRequestDTO;
import com.stockflow_backend.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequestDTO categoryRequestDTO);

}
