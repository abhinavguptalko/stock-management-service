package com.stock.management.junit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.stock.management.controller.StockManagementController;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.service.StockManagementService;

@ExtendWith(MockitoExtension.class)
class StockManagementControllerTest {

    @Mock
    private StockManagementService stockManagementService;

    @InjectMocks
    private StockManagementController stockManagementController;

    private String userId;

    @BeforeEach
    void setup() {
        userId = "user123";
    }

	/*
	 * @Test void testAddStock() { StockDTO stockDTO = new StockDTO("AAPL", 10);
	 * UserStockDTO expectedResponse = new UserStockDTO(userId, "AAPL", 10, 150.0);
	 * 
	 * when(stockManagementService.addStock(eq(userId), any(StockDTO.class)))
	 * .thenReturn(expectedResponse);
	 * 
	 * ResponseEntity<UserStockDTO> response =
	 * stockManagementController.addStock(userId, stockDTO);
	 * 
	 * assertEquals(200, response.getStatusCodeValue());
	 * assertEquals(expectedResponse, response.getBody());
	 * 
	 * verify(stockManagementService, times(1)).addStock(eq(userId),
	 * any(StockDTO.class)); }
	 */

	/*
	 * @Test void testRemoveStock() { Long stockId = 1L;
	 * 
	 * doNothing().when(stockManagementService).removeStock(stockId, userId);
	 * 
	 * ResponseEntity<Void> response =
	 * stockManagementController.removeStock(stockId, userId);
	 * 
	 * assertEquals(204, response.getStatusCodeValue());
	 * 
	 * verify(stockManagementService, times(1)).removeStock(stockId, userId); }
	 */

    @Test
    void testGetAllStocks() {
        List<UserStockDTO> expectedStocks = Arrays.asList(
                new UserStockDTO(userId, "AAPL", 10, 150.0),
                new UserStockDTO(userId, "GOOGL", 5, 2800.0)
        );

        when(stockManagementService.getStocksByUser(userId)).thenReturn(expectedStocks);

        ResponseEntity<List<UserStockDTO>> response = stockManagementController.getAllStocks(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedStocks, response.getBody());

        verify(stockManagementService, times(1)).getStocksByUser(userId);
    }

    @Test
    void testGetPortfolioValue() {
        double expectedValue = 3500.0;

        when(stockManagementService.calculatePortfolioValue(userId)).thenReturn(expectedValue);

        ResponseEntity<Double> response = stockManagementController.getPortfolioValue(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedValue, response.getBody());

        verify(stockManagementService, times(1)).calculatePortfolioValue(userId);
    }
}
