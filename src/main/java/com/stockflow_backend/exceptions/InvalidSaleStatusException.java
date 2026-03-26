package com.stockflow_backend.exceptions;

public class InvalidSaleStatusException extends RuntimeException {
    public InvalidSaleStatusException(String message) {
        super(message);
    }
}
