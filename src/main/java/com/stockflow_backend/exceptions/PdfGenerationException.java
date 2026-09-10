package com.stockflow_backend.exceptions;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message) {
        super(message);
    }
}
