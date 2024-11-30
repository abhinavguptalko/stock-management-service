package com.stock.management.junit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.stock.management.controller.StockManagementController;
import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.service.StockManagementService;

class StockManagementControllerTest {

    @Mock
    private StockManagementService stockManagementService;

    @InjectMocks
    private StockManagementController stockManagementController;

    private static final String USER_ID = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddStock_Success() {
        // Arrange
        String userId = "testUser";
        StockDTO stockDTO = new StockDTO("AAPL", 10);

        // No need to mock void methods with doNothing; ensure dependent methods return correctly
        when(stockManagementService.addOrUpdateStock(userId, stockDTO)).thenReturn(new StockDetails());

        // Act
        ResponseEntity<Void> response = stockManagementController.addStock(userId, stockDTO);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(stockManagementService, times(1)).addOrUpdateStock(userId, stockDTO);
    }


    @Test
    void testRemoveStock() {
        StockDTO stockDTO = new StockDTO("AAPL", 5);
        doNothing().when(stockManagementService).removeStock(USER_ID, stockDTO);

        ResponseEntity<Void> response = stockManagementController.removeStock(USER_ID, stockDTO);

        assertEquals(204, response.getStatusCodeValue());
        verify(stockManagementService, times(1)).removeStock(USER_ID, stockDTO);
    }

    @Test
    void testGetAllStocks() {
        List<UserStockDTO> stocks = Arrays.asList(
                new UserStockDTO(USER_ID, "AAPL", 10, 150.0, 1500.0),
                new UserStockDTO(USER_ID, "GOOGL", 5, 2800.0, 14000.0)
        );
        when(stockManagementService.getStocksByUser(USER_ID)).thenReturn(stocks);

        ResponseEntity<List<UserStockDTO>> response = stockManagementController.getAllStocks(USER_ID);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(stockManagementService, times(1)).getStocksByUser(USER_ID);
    }

    @Test
    void testGetPortfolioValue() {
        double portfolioValue = 15500.0;
        when(stockManagementService.calculatePortfolioValue(USER_ID)).thenReturn(portfolioValue);

        ResponseEntity<Double> response = stockManagementController.getPortfolioValue(USER_ID);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(portfolioValue, response.getBody());
        verify(stockManagementService, times(1)).calculatePortfolioValue(USER_ID);
    }
}
