package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.entities.Product;
import com.stockflow_backend.exceptions.InvalidStockException;
import com.stockflow_backend.exceptions.ProductNotFoundException;
import com.stockflow_backend.mapper.ProductMapper;
import com.stockflow_backend.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // Services
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
    }

    @Transactional
    public void createProduct(ProductRequestDTO productRequestDTO){

        Category category = categoryService.getCategoryId(productRequestDTO.getCategoryId());

        Product product = productMapper.toProduct(productRequestDTO);

        product.setCategory(category);

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<Product> getActiveProducts(Pageable pageable){
        return productRepository.findByActiveTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id){
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("El producto solicitado no existe."));
    }

    @Transactional(readOnly = true)
    public Product getProductByBarcode(String barcode){
        return productRepository.findByBarcodeAndActiveTrue(barcode)
                .orElseThrow(() -> new ProductNotFoundException("El producto con ese código de barras no existe."));
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByName(String name, Pageable pageable){
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByBrand(String brand, Pageable pageable){
        return productRepository.findByBrandContainingIgnoreCaseAndActiveTrue(brand, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable){
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequestDTO dto){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("El producto solicitado no existe."));

        Category category = categoryService.getCategoryId(id);

        product.setName(dto.getName());
        product.setBarcode(dto.getBarcode());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("El producto solicitado no existe."));

        product.setActive(false);

        productRepository.save(product);
    }

    @Transactional
    public void discountStock(Long id, Integer quantity) {

        Product product = getProductById(id);

        if (product.getStock() < quantity) {
            throw new InvalidStockException(
                    String.format("Stock insuficiente para el producto '%s'. Disponible: %d, Solicitado: %d",
                            product.getName(), product.getStock(), quantity)
            );
        }

        product.setStock(product.getStock() - quantity);

        productRepository.save(product);
    }

    @Transactional
    public void addStock(Long id, Integer quantity) {

        if (quantity <= 0){
            throw new InvalidStockException(
                    String.format("La cantidad ingresada no puede ser igual o menor a 0")
            );
        }

        Product product = getProductById(id);

        product.setStock(product.getStock() + quantity);

        productRepository.save(product);

    }



}
