package com.stock.management.external.service;

public interface StockFetchingStrategy {
	
	Double getStockPrice(String symbol);

}
