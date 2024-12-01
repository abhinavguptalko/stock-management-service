package com.stock.management.junit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stock.management.controller.StockHistoryController;
import com.stock.management.dto.StockHistoryDTO;
import com.stock.management.service.StockHistoryService;

public class StockHistoryControllerTest {

    @Mock
    private StockHistoryService stockHistoryService;

    @InjectMocks
    private StockHistoryController stockHistoryController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetStockHistory() {
        String userId = "user1";
        StockHistoryDTO stock1 = new StockHistoryDTO("AAPL", LocalDate.of(2023, 1, 15), 10, "Added");
        StockHistoryDTO stock2 = new StockHistoryDTO("GOOGL", LocalDate.of(2023, 2, 20), 5, "Removed");

        List<StockHistoryDTO> stockHistoryList = Arrays.asList(stock1, stock2);
        when(stockHistoryService.getStockHistory(userId)).thenReturn(stockHistoryList);

        List<StockHistoryDTO> result = stockHistoryController.getStockHistory(userId);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).symbol());
        assertEquals(LocalDate.of(2023, 1, 15), result.get(0).purchaseDate());
        assertEquals(10, result.get(0).quantityChanged());
        assertEquals("Added", result.get(0).action());

        assertEquals("GOOGL", result.get(1).symbol());
        assertEquals(LocalDate.of(2023, 2, 20), result.get(1).purchaseDate());
        assertEquals(5, result.get(1).quantityChanged());
        assertEquals("Removed", result.get(1).action());
    }
}
