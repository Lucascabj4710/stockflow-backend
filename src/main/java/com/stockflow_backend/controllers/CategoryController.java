package com.stockflow_backend.controllers;

import com.stockflow_backend.dto.request.CategoryRequestDTO;
import com.stockflow_backend.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO){
        categoryService.createCategory(categoryRequestDTO);
        return new ResponseEntity<>("COMPLETED", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryByID(@PathVariable Long id){
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories(@RequestParam(defaultValue = "0") Integer pageNumber){

        Pageable pageable = PageRequest.of(pageNumber, 10);

        return new ResponseEntity<>(categoryService.getAllCategories(pageable), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCategoryByID(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("CATEGORY DELETED", HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> editCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO){
        categoryService.editCategory(id, categoryRequestDTO);
        return new ResponseEntity<>("CATEGORY EDITED", HttpStatus.OK);
    }

}
