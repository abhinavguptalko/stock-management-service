package com.stock.management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.data.jpa.repository.StockManagementRepository;
import com.stock.management.dto.StockHistoryDTO;
import com.stock.management.exception.ResourceNotFoundException;

@Service
public class StockHistoryService {

    private final StockManagementRepository stockManagementRepository;

    @Autowired
    public StockHistoryService(StockManagementRepository stockManagementRepository) {
        this.stockManagementRepository = stockManagementRepository;
    }

    /**
     * Fetches the complete stock history for a user.
     *
     * @param userId the user ID
     * @return a list of stock history records
     */
    public List<StockHistoryDTO> getStockHistory(String userId) {
        // Fetch all stock details (both active and expired) for the given user
        List<StockDetails> stockDetailsList = stockManagementRepository.findByUserInfoUserId(userId);

        if (stockDetailsList.isEmpty()) {
            throw new ResourceNotFoundException("No stock history found for user with ID: " + userId);
        }

        // Create a list of StockHistoryDTO objects to represent each stock change
        return stockDetailsList.stream()
                .map(stock -> {
                    // Determine the action (Added or Removed) based on the quantity fields
                    String action = stock.getNewStockQuantity() > 0 ? "Added" : "Removed";
                    int quantityChanged = (stock.getNewStockQuantity() > 0) ? stock.getNewStockQuantity() :
                            stock.getRemovedStockQuantity();

                    // Return a StockHistoryDTO object with the required details
                    return new StockHistoryDTO(stock.getSymbol(), stock.getPurchaseDate(), quantityChanged, action);
                })
                .collect(Collectors.toList());
    }
}
