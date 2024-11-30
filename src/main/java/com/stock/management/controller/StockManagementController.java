package com.stock.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;
import com.stock.management.service.StockManagementService;

@RestController
@RequestMapping("/api/users/{userId}/stocks")
@CrossOrigin("*")
public class StockManagementController {
    private final StockManagementService stockManagementService;

    @Autowired
    public StockManagementController(StockManagementService stockManagementService) {
        this.stockManagementService = stockManagementService;
    }

    @PostMapping
    public ResponseEntity<Void> addStock(@PathVariable String userId, @RequestBody StockDTO stockDTO) {
    	stockManagementService.addOrUpdateStock(userId, stockDTO);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/removeStock")
    public ResponseEntity<Void> removeStock(@PathVariable String userId,@RequestBody StockDTO stockDTO) {
        stockManagementService.removeStock(userId, stockDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserStockDTO>> getAllStocks(@PathVariable String userId) {
        return ResponseEntity.ok(stockManagementService.getStocksByUser(userId));
    }

    @GetMapping("/portfolio/value")
    public ResponseEntity<Double> getPortfolioValue(@PathVariable String userId) {
        return ResponseEntity.ok(stockManagementService.calculatePortfolioValue(userId));
    }
}
