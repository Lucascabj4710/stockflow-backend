package com.stockflow_backend;

import com.stockflow_backend.dto.request.CategoryRequestDTO;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.mapper.CategoryMapper;
import com.stockflow_backend.repositories.CategoryRepository;
import com.stockflow_backend.services.CategoryService;
import org.aspectj.lang.annotation.Before;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO();
    Category category = new Category();

    @BeforeEach
    public void before(){
        categoryRequestDTO.setName("Bebidas");
        category.setName("Bebidas");
    }

    @Test
    public void createCategoryTest(){
        when(categoryMapper.toCategory(categoryRequestDTO)).thenReturn(category);
        Category category1 = new Category();
        category1.setName("Bebidas");

        Category categoryBD = categoryMapper.toCategory(categoryRequestDTO);

        assertEquals(category1.getName(), categoryBD.getName());
    }

}
