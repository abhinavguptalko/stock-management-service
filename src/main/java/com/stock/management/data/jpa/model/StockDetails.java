package com.stock.management.data.jpa.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String symbol;

	@Column(nullable = false)
	private int existingStockQuantity;

	@Column(nullable = false)
	private int newStockQuantity;
	
	@Column(nullable = false)
	private int removedStockQuantity;

	@Column(nullable = false)
	private int totalStockQuantity;

	@Column(nullable = false)
	private LocalDate purchaseDate;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private boolean isExpired;

	@Column(name = "expired_date") // Map to the correct column name
	private LocalDate expiredDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserInfo userInfo;

	// Helper method for total value
	public double getTotalValue() {
		return totalStockQuantity * price;
	}
}
