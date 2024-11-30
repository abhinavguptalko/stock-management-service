package com.stock.management.integration.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.management.controller.StockManagementController;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.service.StockManagementService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StockManagementController.class)
public class StockManagementIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StockManagementService stockManagementService;

	private ObjectMapper objectMapper;
	private StockDTO stockDTO;
	private UserStockDTO userStockDTO;
	private String userId;

	@BeforeEach
	public void setUp() {
		// Initialize StockDTO and UserStockDTO with valid test data
		stockDTO = new StockDTO("AAPL", 10); // Valid stock data
		userStockDTO = new UserStockDTO("AAPL", "user123", 10, 150.0, 1500.0);
		userId = "user123";
		objectMapper = new ObjectMapper();

		// Ensure the MockMvc setup uses the controller bean
		mockMvc = MockMvcBuilders.standaloneSetup(new StockManagementController(stockManagementService)).build();
	}

	@Test
	public void testAddStock() throws Exception {

		mockMvc.perform(post("/api/users/1/stocks").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(stockDTO))).andExpect(status().isNoContent());
	}

	@Test
	public void testRemoveStock() throws Exception {

		mockMvc.perform(put("/api/users/1/stocks/removeStock").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(stockDTO))).andExpect(status().isNoContent());
	}

	@Test
    public void testGetAllStocks() throws Exception {
        when(stockManagementService.getStocksByUser("1")).thenReturn(Collections.singletonList(userStockDTO));

        mockMvc.perform(get("/api/users/1/stocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

	@Test
    public void testGetPortfolioValue() throws Exception {
        when(stockManagementService.calculatePortfolioValue("1")).thenReturn(1000.0);

        mockMvc.perform(get("/api/users/1/stocks/portfolio/value")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
