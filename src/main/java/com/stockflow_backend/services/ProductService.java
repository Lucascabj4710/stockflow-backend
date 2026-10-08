package com.stockflow_backend.services;

import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.dto.response.ProductResponseDto;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.entities.Product;
import com.stockflow_backend.exceptions.CategoryNotFoundException;
import com.stockflow_backend.exceptions.InvalidDiscountException;
import com.stockflow_backend.exceptions.InvalidStockException;
import com.stockflow_backend.exceptions.ProductNotFoundException;
import com.stockflow_backend.mapper.ProductMapper;
import com.stockflow_backend.repositories.CategoryRepository;
import com.stockflow_backend.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public void createProduct(ProductRequestDTO productRequestDTO){

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(()-> new CategoryNotFoundException("The requested category does not exist."));

        Product product = productMapper.toProduct(productRequestDTO);

        BigDecimal discount = productRequestDTO.getDiscount();

        if (discount == null) {
            product.setDiscount(BigDecimal.ZERO);
        } else {
            product.setDiscount(discount);
        }

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

            BigDecimal discountedPrice  = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

            productResponseDto.setDiscountedPrice(discountedPrice);
            productResponseDto.setDiscount(product.getDiscount());

            return productResponseDto;
        });
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("The requested product does not exist."));

        ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);

        if (product.getCategory() != null) {
            productResponseDto.setCategoryName(product.getCategory().getName());
        }

        BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

        productResponseDto.setDiscountedPrice(discountedPrice);
        productResponseDto.setDiscount(product.getDiscount());

        return productResponseDto;
    }

    @Transactional(readOnly = true)
    public Product getProductByIdPrivate(Long id){
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

        BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

        productResponseDto.setDiscountedPrice(discountedPrice);
        productResponseDto.setDiscount(product.getDiscount());

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

                    BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

                    productResponseDto.setDiscountedPrice(discountedPrice);
                    productResponseDto.setDiscount(product.getDiscount());

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
                    BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

                    productResponseDto.setDiscountedPrice(discountedPrice);
                    productResponseDto.setDiscount(product.getDiscount());

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

                    BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

                    productResponseDto.setDiscount(product.getDiscount());

                    productResponseDto.setDiscountedPrice(discountedPrice);

                    return productResponseDto;
                });
    }

    @Transactional
    public void updateProduct(Long id, ProductRequestDTO dto){

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("The requested product does not exist."));


        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new CategoryNotFoundException("The requested category does not exist."));

        product.setName(dto.getName());
        product.setBarcode(dto.getBarcode());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);
        product.setDiscount(
                dto.getDiscount() != null
                        ? dto.getDiscount()
                        : BigDecimal.ZERO
        );
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

        Product product = getProductByIdPrivate(id);

        product.setStock(product.getStock() + quantity);

        productRepository.save(product);

    }

    @Transactional
    public void updateProductStatus(Long productId){

        Product product = getProductByIdPrivate(productId);

        if (product.getStock() <= 0) {
            product.setActive(false);
        } else {
            product.setActive(true);
        }
    }

    @Transactional
    public void applyDiscountToProduct(Long productId, BigDecimal discount) {

        if (discount == null ||
                discount.compareTo(BigDecimal.ZERO) < 0 ||
                discount.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new InvalidDiscountException(
                    "Discount must be between 0 and 100."
            );
        }

        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "The requested product does not exist."
                ));

        product.setDiscount(discount);
    }

    @Transactional
    public void applyDiscountToBrand(String brand, BigDecimal discount) {

        if (discount == null ||
                discount.compareTo(BigDecimal.ZERO) < 0 ||
                discount.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new InvalidDiscountException(
                    "Discount must be between 0 and 100."
            );
        }

        List<Product> products =
                productRepository.findByBrandIgnoreCaseAndActiveTrue(brand);

        if (products.isEmpty()) {
            throw new ProductNotFoundException(
                    "No active products were found for the requested brand."
            );
        }

        products.forEach(product -> product.setDiscount(discount));
    }

    @Transactional
    public void applyDiscountByCategory(Long categoryId, BigDecimal discount) {

        if (discount == null ||
                discount.compareTo(BigDecimal.ZERO) < 0 ||
                discount.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new InvalidDiscountException(
                    "Discount must be between 0 and 100."
            );
        }

        List<Product> products =
                productRepository.findByCategoryIdAndActiveTrue(categoryId);

        if (products.isEmpty()) {
            throw new ProductNotFoundException(
                    "No active products were found in the requested category."
            );
        }

        products.forEach(product -> product.setDiscount(discount));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getLowStockProducts(Integer stock, Pageable pageable){
        return productRepository.findByStockLessThanEqualAndActiveTrue(stock, pageable).map(product -> {
            ProductResponseDto productResponseDto = productMapper.toProductResponseDto(product);

            if (product.getCategory() != null) {
                productResponseDto.setCategoryName(product.getCategory().getName());
            }

            BigDecimal discountedPrice = calculateDiscountedPrice(product.getPrice(), product.getDiscount());

            productResponseDto.setDiscount(product.getDiscount());

            productResponseDto.setDiscountedPrice(discountedPrice);

            return productResponseDto;
        });
    }

    public BigDecimal getDiscountedPrice(Product product) {
        return calculateDiscountedPrice(
                product.getPrice(),
                product.getDiscount()
        );
    }

    private BigDecimal calculateDiscountedPrice(
            BigDecimal price,
            BigDecimal discount) {

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0) {
            return price.subtract(
                    price.multiply(discount)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
        }

        return price;
    }

}