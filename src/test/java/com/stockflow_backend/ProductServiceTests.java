package com.stockflow_backend;
import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.dto.response.ProductResponseDto;
import com.stockflow_backend.entities.Category;
import com.stockflow_backend.entities.Product;
import com.stockflow_backend.exceptions.InvalidDiscountException;
import com.stockflow_backend.exceptions.InvalidStockException;
import com.stockflow_backend.exceptions.ProductNotFoundException;
import com.stockflow_backend.mapper.ProductMapper;
import com.stockflow_backend.repositories.CategoryRepository;
import com.stockflow_backend.repositories.ProductRepository;

import com.stockflow_backend.services.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;


    @Test
    void getProductById_shouldReturnProduct() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Coca Cola");
        product.setPrice(new BigDecimal("1000"));
        product.setDiscount(BigDecimal.ZERO);

        ProductResponseDto response = new ProductResponseDto();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toProductResponseDto(product))
                .thenReturn(response);


        ProductResponseDto result = productService.getProductById(1L);


        assertNotNull(result);

        verify(productRepository).findByIdAndActiveTrue(1L);
        verify(productMapper).toProductResponseDto(product);
    }


    @Test
    void getProductById_shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(1L)
        );
    }


    @Test
    void getProductByIdPrivate_shouldReturnProduct() {

        Product product = new Product();

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));


        Product result = productService.getProductByIdPrivate(1L);


        assertNotNull(result);
    }


    @Test
    void getProductByIdPrivate_shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductByIdPrivate(1L)
        );
    }


    @Test
    void addStock_shouldIncreaseStock() {

        Product product = new Product();
        product.setId(1L);
        product.setStock(10);

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));


        productService.addStock(1L, 5);


        assertEquals(15, product.getStock());

        verify(productRepository).save(product);
    }


    @Test
    void addStock_shouldThrowExceptionWhenQuantityIsZero() {

        assertThrows(
                InvalidStockException.class,
                () -> productService.addStock(1L, 0)
        );

        verify(productRepository, never()).save(any());
    }


    @Test
    void addStock_shouldThrowExceptionWhenQuantityIsNegative() {

        assertThrows(
                InvalidStockException.class,
                () -> productService.addStock(1L, -5)
        );

        verify(productRepository, never()).save(any());
    }


    @Test
    void discountStock_shouldDecreaseStock() {

        Product product = new Product();
        product.setStock(10);

        productService.discountStock(product, 3);


        assertEquals(7, product.getStock());

        verify(productRepository).save(product);
    }


    @Test
    void discountStock_shouldThrowExceptionWhenThereIsNotEnoughStock() {

        Product product = new Product();
        product.setName("Coca Cola");
        product.setStock(2);


        assertThrows(
                InvalidStockException.class,
                () -> productService.discountStock(product, 5)
        );

        verify(productRepository, never()).save(product);
    }


    @Test
    void applyDiscountToProduct_shouldApplyDiscount() {

        Product product = new Product();
        product.setDiscount(BigDecimal.ZERO);

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(product));


        productService.applyDiscountToProduct(
                1L,
                new BigDecimal("20")
        );


        assertEquals(
                new BigDecimal("20"),
                product.getDiscount()
        );
    }


    @Test
    void applyDiscountToProduct_shouldThrowExceptionForInvalidDiscount() {

        assertThrows(
                InvalidDiscountException.class,
                () -> productService.applyDiscountToProduct(
                        1L,
                        new BigDecimal("120")
                )
        );

        verify(productRepository, never())
                .findByIdAndActiveTrue(anyLong());
    }


    @Test
    void applyDiscountToProduct_shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                ProductNotFoundException.class,
                () -> productService.applyDiscountToProduct(
                        1L,
                        new BigDecimal("20")
                )
        );
    }


    @Test
    void getDiscountedPrice_shouldCalculateDiscount() {

        Product product = new Product();

        product.setPrice(new BigDecimal("1000"));
        product.setDiscount(new BigDecimal("20"));


        BigDecimal result =
                productService.getDiscountedPrice(product);


        assertEquals(
                new BigDecimal("800.00"),
                result
        );
    }


    @Test
    void getDiscountedPrice_shouldReturnOriginalPriceWithoutDiscount() {

        Product product = new Product();

        product.setPrice(new BigDecimal("1000"));
        product.setDiscount(BigDecimal.ZERO);


        BigDecimal result =
                productService.getDiscountedPrice(product);


        assertEquals(
                new BigDecimal("1000"),
                result
        );
    }
}
