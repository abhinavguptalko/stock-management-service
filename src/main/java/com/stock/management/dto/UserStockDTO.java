package com.stock.management.dto;

public record UserStockDTO(String userId, String symbol, int quantity, double price, double totalPrice) {
}
