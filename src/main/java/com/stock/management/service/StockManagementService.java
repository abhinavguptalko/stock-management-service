package com.stock.management.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

@Component
public class StockManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockManagementService.class);

    private final StockFetchingStrategy stockFetchingStrategy;
    private final StockManagementRepository stockManagementRepository;
    private final UserSecurityRepository userRepository;

    @Autowired
    public StockManagementService(StockManagementRepository stockManagementRepository,
                                   StockFetchingStrategy stockFetchingStrategy,
                                   UserSecurityRepository userRepository) {
        this.stockFetchingStrategy = stockFetchingStrategy;
        this.stockManagementRepository = stockManagementRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds or updates stock details for a user.
     *
     * @param userId   the user ID
     * @param stockDTO the stock details to add or update
     * @return the updated stock details
     */
    public StockDetails addOrUpdateStock(String userId, StockDTO stockDTO) {
        UserInfo user = fetchUserById(userId);
        int existingQuantity = expireExistingStocks(user, stockDTO.symbol());

        StockDetails newStock = createNewStock(user, stockDTO, existingQuantity + stockDTO.quantity());
        newStock.setNewStockQuantity(stockDTO.quantity());
        LOGGER.info("Stock added/updated for user {}: Symbol={}, Quantity={}",
                userId, stockDTO.symbol(), stockDTO.quantity());
        return stockManagementRepository.save(newStock);
    }

    /**
     * Removes stocks from a user's portfolio.
     *
     * @param userId   the user ID
     * @param stockDTO the stock details to remove
     */
    public void removeStock(String userId, StockDTO stockDTO) {
        UserInfo user = fetchUserById(userId);
        int existingQuantity = expireExistingStocks(user, stockDTO.symbol());

        if (existingQuantity < stockDTO.quantity()) {
            throw new IllegalArgumentException("Cannot remove more stocks than currently held.");
        }

        StockDetails updatedStock = createNewStock(user, stockDTO, existingQuantity - stockDTO.quantity());
        updatedStock.setRemovedStockQuantity(stockDTO.quantity());
        //If All stocks are deleted and expire all the rows
        if (updatedStock.getTotalStockQuantity() == 0) {
            updatedStock.setExpired(true);
            updatedStock.setExpiredDate(LocalDate.now());
        }

        stockManagementRepository.save(updatedStock);
        LOGGER.info("Stock removed for user {}: Symbol={}, Quantity={}",
                userId, stockDTO.symbol(), stockDTO.quantity());
    }

    /**
     * Retrieves active stocks for a user.
     *
     * @param userId the user ID
     * @return a list of active stocks
     */
    public List<UserStockDTO> getStocksByUser(String userId) {
        LOGGER.debug("Fetching active stocks for user {}", userId);

        List<UserStockDTO> userStocks = stockManagementRepository
                .findByUserInfoUserIdAndIsExpiredFalse(userId).stream()
                .map(stock -> {
                    double currentPrice = getCurrentStockPrice(stock.getSymbol());
                    return new UserStockDTO(userId, stock.getSymbol(), stock.getTotalStockQuantity(),
                            currentPrice, currentPrice * stock.getTotalStockQuantity());
                })
                .collect(Collectors.toList());

        if (userStocks.isEmpty()) {
            LOGGER.warn("No active stocks found for user {}", userId);
            throw new ResourceNotFoundException("No active stocks found for user with ID: " + userId);
        }

        return userStocks;
    }

    /**
     * Calculates the total portfolio value for a user.
     *
     * @param userId the user ID
     * @return the total portfolio value
     */
    public double calculatePortfolioValue(String userId) {
        LOGGER.debug("Calculating portfolio value for user {}", userId);

        // Fetch all active stocks for the user
        List<StockDetails> activeStocks = stockManagementRepository.findByUserInfoUserIdAndIsExpiredFalse(userId);

        if (activeStocks.isEmpty()) {
            LOGGER.warn("No active stocks found for user {}", userId);
            throw new ResourceNotFoundException("No active stocks found for user with ID: " + userId);
        }

        // Map to store unique symbols and their fetched prices
        Map<String, Double> symbolPriceMap = activeStocks.stream()
                .map(StockDetails::getSymbol)
                .distinct()
                .collect(Collectors.toMap(
                        symbol -> symbol,
                        this::getCurrentStockPrice
                ));

        // Calculate portfolio value using the fetched prices
        double totalPortfolioValue = activeStocks.stream()
                .mapToDouble(stock -> {
                    double currentPrice = symbolPriceMap.get(stock.getSymbol());
                    return stock.getTotalStockQuantity() * currentPrice;
                })
                .sum();

        LOGGER.info("Calculated portfolio value for user {}: {}", userId, totalPortfolioValue);
        return totalPortfolioValue;
    }


    /**
     * Helper method to fetch user by ID or throw an exception if not found.
     *
     * @param userId the user ID
     * @return the user information
     */
    private UserInfo fetchUserById(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * Marks existing stocks for a user as expired and calculates the current total quantity.
     *
     * @param user   the user information
     * @param symbol the stock symbol
     * @return the total quantity of the stock before expiry
     */
    private int expireExistingStocks(UserInfo user, String symbol) {
        List<StockDetails> activeStocks = stockManagementRepository.findByUserInfoAndSymbolAndIsExpiredFalse(user, symbol);

        int existingQuantity = activeStocks.stream()
                .mapToInt(StockDetails::getTotalStockQuantity)
                .sum();

        activeStocks.forEach(stock -> {
            stock.setExpired(true);
            stock.setExpiredDate(LocalDate.now());
            stockManagementRepository.save(stock);
        });

        return existingQuantity;
    }

    /**
     * Creates a new stock entry.
     *
     * @param user      the user information
     * @param stockDTO  the stock details
     * @param totalQuantity the total quantity of the stock
     * @return a new stock details object
     */
    private StockDetails createNewStock(UserInfo user, StockDTO stockDTO, int totalQuantity) {
        StockDetails stock = new StockDetails();
        stock.setSymbol(stockDTO.symbol());
        stock.setExistingStockQuantity(totalQuantity - stockDTO.quantity());
        stock.setTotalStockQuantity(totalQuantity);
        stock.setPrice(getCurrentStockPrice(stockDTO.symbol()));
        stock.setPurchaseDate(LocalDate.now());
        stock.setUserInfo(user);
        stock.setExpired(false);
        return stock;
    }
    
   

    /**
     * Fetches the current stock price using the strategy pattern.
     *
     * @param symbol the stock symbol
     * @return the current stock price
     */
    private double getCurrentStockPrice(String symbol) {
        return stockFetchingStrategy.getStockPrice(symbol);
    }
}
