package com.stockflow_backend.services;

import com.stockflow_backend.dto.ProductRequestDTO;
import com.stockflow_backend.entities.Product;
import com.stockflow_backend.mapper.ProductMapper;
import com.stockflow_backend.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public void createProduct(ProductRequestDTO productRequestDTO){
        Product product = productMapper.toProduct(productRequestDTO);
        productRepository.save(product);
    }

    public Page<Product> getActiveProducts(Pageable pageable){
        return productRepository.findByActiveTrue(pageable);
    }

    public Product getProductById(Long id){
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException("El producto solicitado no existe."));
    }

    public Product getProductByBarcode(String barcode){
        return productRepository.findByBarcodeAndActiveTrue(barcode)
                .orElseThrow(() -> new NoSuchElementException("El producto con ese código de barras no existe."));
    }

    public Page<Product> getProductsByName(String name, Pageable pageable){
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }

    public Page<Product> getProductsByBrand(String brand, Pageable pageable){
        return productRepository.findByBrandContainingIgnoreCaseAndActiveTrue(brand, pageable);
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable){
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    public Product updateProduct(Long id, ProductRequestDTO dto){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException("El producto solicitado no existe."));

        product.setName(dto.getName());
        product.setBarcode(dto.getBarcode());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new NoSuchElementException("El producto solicitado no existe."));

        product.setActive(false);

        productRepository.save(product);
    }

}
