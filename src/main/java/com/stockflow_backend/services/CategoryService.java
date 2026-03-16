package com.stockflow_backend.services;

import com.stockflow_backend.dto.CategoryRequestDTO;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.mapper.CategoryMapper;
import com.stockflow_backend.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public void createCategory(CategoryRequestDTO categoryRequestDTO){
        Category category = categoryMapper.toCategory(categoryRequestDTO);
        categoryRepository.save(category);
    }

    public Category getCategoryId(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("La categoria solicitada no existe"));
    }

    public Page<Category> getAllCategories(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    public void editCategory(Long id,CategoryRequestDTO categoryRequestDTO){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("La categoria solicitada no existe"));

        category.setName(categoryRequestDTO.getName());
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("La categoria solicitada no existe"));

        categoryRepository.deleteById(id);
    }

}
