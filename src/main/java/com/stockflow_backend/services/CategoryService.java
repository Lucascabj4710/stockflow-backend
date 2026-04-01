package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.CategoryRequestDTO;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.exceptions.CategoryNotFoundException;
import com.stockflow_backend.mapper.CategoryMapper;
import com.stockflow_backend.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public void createCategory(CategoryRequestDTO categoryRequestDTO){
        Category category = categoryMapper.toCategory(categoryRequestDTO);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("The requested category does not exist"));
    }

    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable){
        return categoryRepository.findAll(pageable);
    }

    @Transactional
    public void editCategory(Long id, CategoryRequestDTO categoryRequestDTO){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException("The requested category does not exist"));

        category.setName(categoryRequestDTO.getName());
        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id){
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("The requested category does not exist");
        }
        categoryRepository.deleteById(id);
    }
}