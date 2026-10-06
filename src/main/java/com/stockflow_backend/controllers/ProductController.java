package com.stockflow_backend.controllers;

import com.stockflow_backend.dto.request.ProductRequestDTO;
import com.stockflow_backend.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO){
        productService.createProduct(productRequestDTO);

        return new ResponseEntity<>("CREATED SUCCESSFULL", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductByID(@PathVariable Long id){
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        int page = (pageNumber < 0) ? 0 : pageNumber;

        Pageable pageable = PageRequest.of(page, 10);

        return new ResponseEntity<>(
                productService.getActiveProducts(pageable),
                HttpStatus.OK
        );
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode){
        return new ResponseEntity<>(
                productService.getProductByBarcode(barcode),
                HttpStatus.OK
        );
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProductByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") Integer pagenumber
    ){

        Pageable pageable = PageRequest.of(pagenumber, 10);

        return new ResponseEntity<>(
                productService.getProductsByName(name, pageable),
                HttpStatus.OK
        );
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<?> getProductsByBrand(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        Pageable pageable = PageRequest.of(pageNumber, 10);

        return new ResponseEntity<>(
                productService.getProductsByBrand(brand, pageable),
                HttpStatus.OK
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {
        Pageable pageable = PageRequest.of(pageNumber, 10);

        return new ResponseEntity<>(
                productService.getProductsByCategory(categoryId, pageable),
                HttpStatus.OK
        );
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer stock,
            @RequestParam(defaultValue = "0") Integer pageNumber
    ) {

        int page = (pageNumber < 0) ? 0 : pageNumber;

        Pageable pageable = PageRequest.of(page, 10);

        return new ResponseEntity<>(
                productService.getLowStockProducts(stock, pageable),
                HttpStatus.OK
        );
    }

    @PatchMapping("/discount/{productId}")
    public ResponseEntity<?> applyDiscountToProduct(
            @PathVariable Long productId,
            @RequestParam BigDecimal discount
    ) {

        productService.applyDiscountToProduct(productId, discount);

        return ResponseEntity.ok("Discount applied to product");
    }

    @PatchMapping("/discount/brand/{brand}")
    public ResponseEntity<?> applyDiscountToBrand(
            @PathVariable String brand,
            @RequestParam BigDecimal discount
    ) {

        productService.applyDiscountToBrand(brand, discount);

        return ResponseEntity.ok("Discount applied to brand products");
    }

    @PatchMapping("/discount/category/{categoryId}")
    public ResponseEntity<?> applyDiscountByCategory(
            @PathVariable Long categoryId,
            @RequestParam BigDecimal discount
    ) {

        productService.applyDiscountByCategory(categoryId, discount);

        return ResponseEntity.ok("Discount applied to category products");
    }

    @PutMapping("/update/{productID}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long productID,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ){

        productService.updateProduct(productID, productRequestDTO);

        return ResponseEntity.ok("Updated product");
    }

    @PatchMapping("/stock/{id}/{quantity}")
    public ResponseEntity<?> addStock(
            @PathVariable(required = true) Long id,
            @PathVariable(required = true) Integer quantity
    ){

        productService.addStock(id, quantity);

        return new ResponseEntity<>("Updated stock", HttpStatus.OK);
    }

    @PatchMapping("/updateStatus/{productID}")
    public ResponseEntity<?> updateStatus(@PathVariable Long productID){

        productService.updateProductStatus(productID);

        return ResponseEntity.ok("Updated status");
    }
}
