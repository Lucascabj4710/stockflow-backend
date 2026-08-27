package com.stockflow_backend.controllers;

import com.stockflow_backend.services.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/pdf"))
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/{saleID}")
    public ResponseEntity<byte[]> createPDF(@PathVariable Long saleID){

        byte[] pdf = pdfService.createPdf(saleID);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=archivo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
