package com.stock.management.junit.external.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.stock.management.exception.NoDataFoundForSymbolException;
import com.stock.management.external.service.impl.AlphaVantageStockFetchingStrategy;

class AlphaVantageStockFetchingStrategyTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AlphaVantageStockFetchingStrategy stockFetchingStrategy;

    public AlphaVantageStockFetchingStrategyTest() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testGetStockPrice_Success() {
        // Arrange
        String symbol = "AAPL";
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=demo";

        Map<String, Object> mockResponse = new HashMap<>();
        Map<String, String> globalQuote = new HashMap<>();
        globalQuote.put("05. price", "150.50");
        mockResponse.put("Global Quote", globalQuote);

        when(restTemplate.getForObject(url, Map.class)).thenReturn(mockResponse);

        // Act
        Double price = stockFetchingStrategy.getStockPrice(symbol);

        // Assert
        assertNotNull(price);
        assertEquals(150.50, price);
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    void testGetStockPrice_NoDataFound() {
        // Arrange
        String symbol = "INVALID";
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=demo";

        when(restTemplate.getForObject(url, Map.class)).thenReturn(null);

        // Act & Assert
        NoDataFoundForSymbolException exception = assertThrows(NoDataFoundForSymbolException.class, () -> {
            stockFetchingStrategy.getStockPrice(symbol);
        });

        assertEquals("No data found for symbol: INVALID", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    void testGetStockPrice_InvalidResponseStructure() {
        // Arrange
        String symbol = "AAPL";
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=demo";

        Map<String, Object> mockResponse = new HashMap<>(); // Missing "Global Quote"

        when(restTemplate.getForObject(url, Map.class)).thenReturn(mockResponse);

        // Act & Assert
        NoDataFoundForSymbolException exception = assertThrows(NoDataFoundForSymbolException.class, () -> {
            stockFetchingStrategy.getStockPrice(symbol);
        });

        assertEquals("No data found for symbol: AAPL", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }
}
