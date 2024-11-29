package com.stock.management.dto;

import lombok.Getter;
import lombok.Setter;


public class StockDTO {
    private String symbol;
    private int quantity;

    public StockDTO(String symbol, int quantity) {
        this.symbol = symbol;
        this.quantity = quantity;
    }

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
    
    
}
