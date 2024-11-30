package com.stock.management.junit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.StockManagementRepository;
import com.stock.management.data.jpa.repository.UserSecurityRepository;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.exception.ResourceNotFoundException;
import com.stock.management.external.service.StockFetchingStrategy;
import com.stock.management.service.StockManagementService;

class StockManagementServiceTest {

    private static final String USER_ID = "user123";
    private static final String SYMBOL_AAPL = "AAPL";
    private static final String SYMBOL_GOOGL = "GOOGL";
    
    @Mock
    private StockFetchingStrategy stockFetchingStrategy;

    @Mock
    private StockManagementRepository stockManagementRepository;

    @Mock
    private UserSecurityRepository userRepository;

    @InjectMocks
    private StockManagementService stockManagementService;

    private UserInfo user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserInfo();
        user.setUserId(USER_ID);
    }

    @Test
    void testAddOrUpdateStock_Success() {
        // Arrange
        StockDTO stockDTO = new StockDTO(SYMBOL_AAPL, 10);
        StockDetails newStock = new StockDetails();
        newStock.setSymbol(SYMBOL_AAPL);
        newStock.setTotalStockQuantity(10);
        newStock.setPrice(150.0);

        setupUserAndMocks(SYMBOL_AAPL, 0, 150.0);

        when(stockManagementRepository.save(any(StockDetails.class))).thenReturn(newStock);

        // Act
        StockDetails result = stockManagementService.addOrUpdateStock(USER_ID, stockDTO);

        // Assert
        assertNotNull(result);
        assertEquals(SYMBOL_AAPL, result.getSymbol());
        assertEquals(10, result.getTotalStockQuantity());
        assertEquals(150.0, result.getPrice());
        verify(stockManagementRepository, times(2)).save(any(StockDetails.class));  // Expecting two save calls
    }



    @Test
    void testRemoveStock_Success() {
        // Arrange
        String userId = "user123";
        StockDTO stockDTO = new StockDTO("AAPL", 5);
        UserInfo user = new UserInfo();
        user.setUserId(userId);

        StockDetails existingStock = new StockDetails();
        existingStock.setSymbol("AAPL");
        existingStock.setTotalStockQuantity(10);
        existingStock.setExpired(false);

        // Mock dependencies
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(user));
        when(stockManagementRepository.findByUserInfoAndSymbolAndIsExpiredFalse(user, "AAPL"))
            .thenReturn(Collections.singletonList(existingStock));
        when(stockFetchingStrategy.getStockPrice("AAPL")).thenReturn(150.0);

        // Act
        stockManagementService.removeStock(userId, stockDTO);

        // Assert
        verify(stockManagementRepository, times(2)).save(any(StockDetails.class)); // Expecting 2 save calls
    }


    @Test
    void testRemoveStock_ExceedsQuantity() {
        // Arrange
        StockDTO stockDTO = new StockDTO(SYMBOL_AAPL, 15);
        StockDetails existingStock = new StockDetails();
        existingStock.setSymbol(SYMBOL_AAPL);
        existingStock.setTotalStockQuantity(10);

        setupUserAndMocks(SYMBOL_AAPL, 10, 150.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stockManagementService.removeStock(USER_ID, stockDTO);
        });

        assertEquals("Cannot remove more stocks than currently held.", exception.getMessage());
    }

    @Test
    void testGetStocksByUser_Success() {
        // Arrange
        StockDetails stock1 = new StockDetails();
        stock1.setSymbol(SYMBOL_AAPL);
        stock1.setTotalStockQuantity(10);

        StockDetails stock2 = new StockDetails();
        stock2.setSymbol(SYMBOL_GOOGL);
        stock2.setTotalStockQuantity(5);

        setupUserAndMocksForMultipleStocks(Arrays.asList(stock1, stock2), 150.0, 2800.0);

        // Act
        List<UserStockDTO> stocks = stockManagementService.getStocksByUser(USER_ID);

        // Assert
        assertNotNull(stocks);
        assertEquals(2, stocks.size());
        assertEquals(SYMBOL_AAPL, stocks.get(0).symbol());
        assertEquals(1500.0, stocks.get(0).totalPrice());
        assertEquals(SYMBOL_GOOGL, stocks.get(1).symbol());
        assertEquals(14000.0, stocks.get(1).totalPrice());
    }

    @Test
    void testGetStocksByUser_NoActiveStocks() {
        // Arrange
        setupUserAndMocksForEmptyStocks();

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            stockManagementService.getStocksByUser(USER_ID);
        });

        assertEquals("No active stocks found for user with ID: " + USER_ID, exception.getMessage());
    }

    @Test
    void testCalculatePortfolioValue_Success() {
        // Arrange
        StockDetails stock1 = new StockDetails();
        stock1.setSymbol(SYMBOL_AAPL);
        stock1.setTotalStockQuantity(10);

        StockDetails stock2 = new StockDetails();
        stock2.setSymbol(SYMBOL_GOOGL);
        stock2.setTotalStockQuantity(5);

        setupUserAndMocksForMultipleStocks(Arrays.asList(stock1, stock2), 150.0, 2800.0);

        // Act
        double portfolioValue = stockManagementService.calculatePortfolioValue(USER_ID);

        // Assert
        assertEquals(15500.0, portfolioValue);
    }

    @Test
    void testCalculatePortfolioValue_NoStocks() {
        // Arrange
        setupUserAndMocksForEmptyStocks();

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            stockManagementService.calculatePortfolioValue(USER_ID);
        });

        assertEquals("No active stocks found for user with ID: " + USER_ID, exception.getMessage());
    }

    // Helper Methods
    private void setupUserAndMocks(String symbol, int existingQuantity, double stockPrice) {
        StockDetails existingStock = new StockDetails();
        existingStock.setSymbol(symbol);
        existingStock.setTotalStockQuantity(existingQuantity);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));
        when(stockManagementRepository.findByUserInfoAndSymbolAndIsExpiredFalse(user, symbol))
            .thenReturn(Collections.singletonList(existingStock));
        when(stockFetchingStrategy.getStockPrice(symbol)).thenReturn(stockPrice);
    }

    private void setupUserAndMocksForMultipleStocks(List<StockDetails> stocks, double... prices) {
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));
        when(stockManagementRepository.findByUserInfoUserIdAndIsExpiredFalse(USER_ID)).thenReturn(stocks);
        
        for (int i = 0; i < stocks.size(); i++) {
            when(stockFetchingStrategy.getStockPrice(stocks.get(i).getSymbol())).thenReturn(prices[i]);
        }
    }

    private void setupUserAndMocksForEmptyStocks() {
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));
        when(stockManagementRepository.findByUserInfoUserIdAndIsExpiredFalse(USER_ID)).thenReturn(Collections.emptyList());
    }
}
