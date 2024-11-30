package com.stock.management.controller;

import com.stock.management.dto.StockHistoryDTO;
import com.stock.management.service.StockHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-history")
@CrossOrigin("*")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    @Autowired
    public StockHistoryController(StockHistoryService stockHistoryService) {
        this.stockHistoryService = stockHistoryService;
    }

    /**
     * Fetches the stock history for a user.
     *
     * @param userId the user ID
     * @return a list of stock history records
     */
    @GetMapping("/{userId}")
    public List<StockHistoryDTO> getStockHistory(@PathVariable String userId) {
        return stockHistoryService.getStockHistory(userId);
    }
}
