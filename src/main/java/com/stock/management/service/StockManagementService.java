package com.stock.management.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.StockManagementRepository;
import com.stock.management.data.jpa.repository.UserSecurityRepository;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.exception.ResourceNotFoundException;
import com.stock.management.external.service.StockFetchingStrategy;

import jakarta.validation.Valid;

@Component
public class StockManagementService {

	private final StockFetchingStrategy stockFetchingStrategy;
	private final StockManagementRepository stockManagementRepository;
	private final UserSecurityRepository userRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(StockManagementService.class);

	@Autowired
	public StockManagementService(StockManagementRepository stockManagementRepository,
			StockFetchingStrategy stockFetchingStrategy, UserSecurityRepository userRepository) {
		this.stockFetchingStrategy = stockFetchingStrategy;
		this.stockManagementRepository = stockManagementRepository;
		this.userRepository = userRepository;
	}

	public void addOrUpdateStock(String userId, @Valid StockDTO stockDTO) {
		// Check if stock with the given symbol exists for the user
		LOGGER.debug("Adding/Updating Stock for user {}", userId);
		UserInfo user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Optional<StockDetails> existingStock = stockManagementRepository.findByUserInfoUserIdAndSymbol(userId,
				stockDTO.getSymbol());

		if (existingStock.isPresent()) {
			// Stock exists, update the quantity
			StockDetails stock = existingStock.get();
			stock.setQuantity(stock.getQuantity() + stockDTO.getQuantity()); // Add the new quantity to existing stock
			stockManagementRepository.save(stock); // Save the updated stock
		} else {
			// Stock doesn't exist, add new stock
			double price = getCurrentStockPrice(stockDTO.getSymbol());
			StockDetails newStock = new StockDetails(stockDTO.getSymbol(), stockDTO.getQuantity(), price, user);
			stockManagementRepository.save(newStock); // Add new stock
		}
	}

	private double getCurrentStockPrice(String symbol) {
		return stockFetchingStrategy.getStockPrice(symbol);
	}

	public void removeStock(String symbol, String userId) {
		LOGGER.debug("Removing Stock for user {}", userId);
		List<StockDetails> stock = stockManagementRepository.findBySymbolAndUserInfoUserId(symbol, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Stock not found or unauthorized"));

		stockManagementRepository.deleteAll(stock);
	}

	public List<UserStockDTO> getStocksByUser(String userId) {
		LOGGER.debug("Getting Stock for user {}", userId);

		List<UserStockDTO> userStocks = stockManagementRepository.findByUserInfoUserId(userId).stream().map(stock -> {
			double currentPrice = getCurrentStockPrice(stock.getSymbol()); // Fetch current stock price
			return new UserStockDTO(userId, stock.getSymbol(), // Stock symbol
					stock.getQuantity(), // Quantity of the stock
					currentPrice // Current price
			);
		}).collect(Collectors.toList()); // Use Collectors.toList() for Java 8 compatibility

		if (userStocks.isEmpty()) {
			LOGGER.warn("No stocks found for user {}", userId);
			throw new ResourceNotFoundException("No stocks found for user with ID: {} " + userId);
		}

		return userStocks;

	}

	public double calculatePortfolioValue(String userId) {
		LOGGER.debug("Calculating Portfolio Value for user {}", userId);
		return stockManagementRepository.findByUserInfoUserId(userId).stream().mapToDouble(StockDetails::getTotalValue)
				.sum();
	}

}
