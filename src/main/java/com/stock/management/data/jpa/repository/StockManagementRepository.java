package com.stock.management.data.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.management.data.jpa.model.StockDetails;

@Repository
public interface StockManagementRepository extends JpaRepository<StockDetails, Long>{

	 List<StockDetails> findByUserInfoUserId(String userId);
	 Optional<StockDetails> findByUserInfoUserIdAndSymbol(String userId, String symbol);
	 Optional<List<StockDetails>> findBySymbolAndUserInfoUserId(String symbol, String userId);
}
