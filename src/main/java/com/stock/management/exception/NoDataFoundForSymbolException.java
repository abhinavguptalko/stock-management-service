package com.stock.management.exception;

public class NoDataFoundForSymbolException extends RuntimeException {
    public NoDataFoundForSymbolException(String symbol) {
        super("No data found for symbol: " + symbol);
    }
}
