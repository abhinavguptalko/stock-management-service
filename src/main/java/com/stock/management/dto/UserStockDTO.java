package com.stock.management.dto;

public class UserStockDTO {

	private String userId;
	 private String symbol;
	    private int quantity;
	    private double price;
	    
		public UserStockDTO(String userId, String symbol, int quantity, double price) {
			super();
			this.userId = userId;
			this.symbol = symbol;
			this.quantity = quantity;
			this.price = price;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
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
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		} 
	    
	    
}
