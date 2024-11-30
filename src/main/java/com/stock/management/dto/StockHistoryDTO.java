package com.stock.management.dto;

import java.time.LocalDate;

public record StockHistoryDTO(String symbol, LocalDate purchaseDate, int quantityChanged, String action // "Added" or
																										// "Removed"
) {
}
