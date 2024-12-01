package com.stock.management.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.management.StockManagementApplication;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.service.StockManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = StockManagementApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class StockManagementIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockManagementService stockManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    private StockDTO stockDTO;
    private UserStockDTO userStockDTO;

    @BeforeEach
    public void setUp() {
        // Initialize StockDTO and UserStockDTO with valid test data
        stockDTO = new StockDTO("AAPL", 10); // Valid stock data
        userStockDTO = new UserStockDTO("AAPL", "user123", 10, 150.0, 1500.0);

        when(stockManagementService.getStocksByUser("1")).thenReturn(Collections.singletonList(userStockDTO));
        when(stockManagementService.calculatePortfolioValue("1")).thenReturn(1000.0);
    }

    @Test
    public void testAddStock() throws Exception {
        mockMvc.perform(post("/api/users/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRemoveStock() throws Exception {
        mockMvc.perform(put("/api/users/1/stocks/removeStock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllStocks() throws Exception {
        mockMvc.perform(get("/api/users/1/stocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(userStockDTO))));
    }

    @Test
    public void testGetPortfolioValue() throws Exception {
        mockMvc.perform(get("/api/users/1/stocks/portfolio/value")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(1000.0)));
    }
}
