package com.stockflow_backend.controllers;

import com.stockflow_backend.dto.request.SaleRequestDTO;
import com.stockflow_backend.services.SaleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/sale")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<String> createSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        saleService.createSale(saleRequestDTO);
        return new ResponseEntity<>("Venta creada con éxito", HttpStatus.CREATED);
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
    public ResponseEntity<String> editSaleStatus(@PathVariable Long saleID, @PathVariable String status) {
        saleService.editSaleStatus(saleID, status);
        return ResponseEntity.ok("Estado de la venta " + saleID + " actualizado a: " + status.toUpperCase());
    }
}

