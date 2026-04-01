package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.dto.response.ProductResponseDto;
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

        Category category = categoryService.getCategoryById(productRequestDTO.getCategoryId());

        Product product = productMapper.toProduct(productRequestDTO);

        product.setCategory(category);

        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getActiveProducts(Pageable pageable){
        return productRepository.findByActiveTrue(pageable).map(product -> {
            ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);

            if (product.getCategory() != null) {
                productResponseDto.setCategoryName(product.getCategory().getName());
            }

            return productResponseDto;
        });
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id){
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("The requested product does not exist."));
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductByBarcode(String barcode){
        Product product = productRepository.findByBarcodeAndActiveTrue(barcode)
                .orElseThrow(() -> new ProductNotFoundException("The product with that barcode does not exist."));

        ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);

        if (product.getCategory() != null) {
            productResponseDto.setCategoryName(product.getCategory().getName());
        }

        return productResponseDto;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByName(String name, Pageable pageable){
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable)
                .map(product -> {
                    ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);
                    if (product.getCategory() != null) {
                        productResponseDto.setCategoryName(product.getCategory().getName());
                    }
                    return productResponseDto;
                });
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByBrand(String brand, Pageable pageable){
        return productRepository.findByBrandContainingIgnoreCaseAndActiveTrue(brand, pageable)
                .map(product -> {
                    ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);
                    if (product.getCategory() != null) {
                        productResponseDto.setCategoryName(product.getCategory().getName());
                    }
                    return productResponseDto;
                });
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, Pageable pageable){
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable)
                .map(product -> {
                    ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);
                    if (product.getCategory() != null) {
                        productResponseDto.setCategoryName(product.getCategory().getName());
                    }
                    return productResponseDto;
                });
    }

    @Transactional
    public void updateProduct(Long id, ProductRequestDTO dto){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("The requested product does not exist."));

        // Se usa el id de categoría del DTO
        Category category = categoryService.getCategoryById(dto.getCategoryId());

        product.setName(dto.getName());
        product.setBarcode(dto.getBarcode());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id){

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException("The requested product does not exist."));

        product.setActive(false);

        productRepository.save(product);
    }

    @Transactional
    public void discountStock(Product product, Integer quantity) {

        if (product.getStock() < quantity) {
            throw new InvalidStockException(
                    String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
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
                    "The quantity entered cannot be less than or equal to 0"
            );
        }

        Product product = getProductById(id);

        product.setStock(product.getStock() + quantity);

        productRepository.save(product);

    }

    @Transactional
    public void updateProductStatus(Long productId){
        Product product = getProductById(productId);

        if (product.getStock() <= 0) {
            product.setActive(false);
        } else {
            product.setActive(true);
        }
    }

}