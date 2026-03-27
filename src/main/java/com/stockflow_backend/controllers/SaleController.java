package com.stockflow_backend.controllers;

import com.stockflow_backend.dto.request.SaleRequestDTO;
import com.stockflow_backend.services.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<String> createSale(@Valid @RequestBody SaleRequestDTO saleRequestDTO) {
        saleService.createSale(saleRequestDTO);
        return new ResponseEntity<>("Sale created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{saleID}")
    public ResponseEntity<?> getSaleByID(@PathVariable Long saleID) {
        return ResponseEntity.ok(saleService.findSaleByID(saleID));
    }

    @GetMapping
    public ResponseEntity<?> getAllSales(@RequestParam(defaultValue = "0") int pageNumber) {
        return ResponseEntity.ok(saleService.getAllSales(pageNumber));
    }

    @PatchMapping("/{saleID}/status/{status}")
    public ResponseEntity<?> editSaleStatus(@PathVariable Long saleID, @PathVariable String status) {
        saleService.editSaleStatus(saleID, status);
        return ResponseEntity.ok("Sale " + saleID + " status updated to: " + status.toUpperCase());
    }

    @PatchMapping("/{saleID}/products/{productID}")
    public ResponseEntity<?> deleteProductSaleByID(@PathVariable Long saleID, @PathVariable Long productID) {
        saleService.deleteProductSaleBySaleID(saleID, productID);
        return ResponseEntity.ok("Sale updated successfully");
    }

}