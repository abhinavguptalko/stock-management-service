package com.stock.management.junit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.data.jpa.model.UserInfo;
import com.stock.management.data.jpa.repository.StockManagementRepository;
import com.stock.management.dto.StockHistoryDTO;
import com.stock.management.exception.ResourceNotFoundException;
import com.stock.management.service.StockHistoryService;

class StockHistoryServiceTest {

    @Mock
    private StockManagementRepository stockManagementRepository;

    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stockHistoryService = new StockHistoryService(stockManagementRepository);
    }

    @Test
    void testGetStockHistory_Success() {
        // Arrange
        String userId = "user123";
        UserInfo mockUser = new UserInfo(); // Or mock(UserInfo.class)
        
        StockDetails stock1 = new StockDetails(
                1L, "AAPL", 0, 10, 0, 10,
                LocalDate.of(2023, 11, 15), 150.0, false, null, mockUser
        );
        StockDetails stock2 = new StockDetails(
                2L, "TSLA", 10, 0, 5, 5,
                LocalDate.of(2023, 12, 1), 750.0, false, null, mockUser
        );

        when(stockManagementRepository.findByUserInfoUserId(userId)).thenReturn(Arrays.asList(stock1, stock2));

        // Act
        List<StockHistoryDTO> result = stockHistoryService.getStockHistory(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        StockHistoryDTO stockHistory1 = result.get(0);
        assertEquals("AAPL", stockHistory1.symbol());
        assertEquals(LocalDate.of(2023, 11, 15), stockHistory1.purchaseDate());
        assertEquals(10, stockHistory1.quantityChanged());
        assertEquals("Added", stockHistory1.action());

        StockHistoryDTO stockHistory2 = result.get(1);
        assertEquals("TSLA", stockHistory2.symbol());
        assertEquals(LocalDate.of(2023, 12, 1), stockHistory2.purchaseDate());
        assertEquals(5, stockHistory2.quantityChanged());
        assertEquals("Removed", stockHistory2.action());

        verify(stockManagementRepository, times(1)).findByUserInfoUserId(userId);
    }

    @Test
    void testGetStockHistory_EmptyList_ThrowsException() {
        // Arrange
        String userId = "user123";
        when(stockManagementRepository.findByUserInfoUserId(userId)).thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            stockHistoryService.getStockHistory(userId);
        });

        assertEquals("No stock history found for user with ID: " + userId, exception.getMessage());
        verify(stockManagementRepository, times(1)).findByUserInfoUserId(userId);
    }

    @Test
    void testGetStockHistory_MixedActions() {
        // Arrange
        String userId = "user123";
        UserInfo mockUser = new UserInfo(); // Or mock(UserInfo.class)

        StockDetails stock1 = new StockDetails(
                1L, "MSFT", 0, 15, 0, 15,
                LocalDate.of(2023, 10, 25), 300.0, false, null, mockUser
        );
        StockDetails stock2 = new StockDetails(
                2L, "AMZN", 10, 0, 8, 2,
                LocalDate.of(2023, 10, 30), 2500.0, false, null, mockUser
        );

        when(stockManagementRepository.findByUserInfoUserId(userId)).thenReturn(Arrays.asList(stock1, stock2));

        // Act
        List<StockHistoryDTO> result = stockHistoryService.getStockHistory(userId);

        // Assert
        assertEquals(2, result.size());

        StockHistoryDTO stockHistory1 = result.get(0);
        assertEquals("MSFT", stockHistory1.symbol());
        assertEquals("Added", stockHistory1.action());
        assertEquals(15, stockHistory1.quantityChanged());

        StockHistoryDTO stockHistory2 = result.get(1);
        assertEquals("AMZN", stockHistory2.symbol());
        assertEquals("Removed", stockHistory2.action());
        assertEquals(8, stockHistory2.quantityChanged());
    }
}
