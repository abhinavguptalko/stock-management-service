package com.stock.management.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.management.StockManagementApplication;
import com.stock.management.dto.StockHistoryDTO;
import com.stock.management.service.StockHistoryService;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = StockManagementApplication.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class StockHistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockHistoryService stockHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        StockHistoryDTO stock1 = new StockHistoryDTO("AAPL", LocalDate.of(2023, 1, 15), 10, "Added");
        StockHistoryDTO stock2 = new StockHistoryDTO("GOOGL", LocalDate.of(2023, 2, 20), 5, "Removed");

        when(stockHistoryService.getStockHistory("user1")).thenReturn(Arrays.asList(stock1, stock2));
    }

    @Test
    public void testGetStockHistory() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/stock-history/user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(
                        new StockHistoryDTO("AAPL", LocalDate.of(2023, 1, 15), 10, "Added"),
                        new StockHistoryDTO("GOOGL", LocalDate.of(2023, 2, 20), 5, "Removed")
                ))));

        resultActions.andReturn();
    }
}
