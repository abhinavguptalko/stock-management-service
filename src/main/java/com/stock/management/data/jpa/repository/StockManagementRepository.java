package com.stock.management.data.jpa.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stock.management.data.jpa.model.StockDetails;
import com.stock.management.data.jpa.model.UserInfo;

@Repository
public interface StockManagementRepository extends JpaRepository<StockDetails, Long> {

	 List<StockDetails> findByUserInfoUserId(String userId);

	List<StockDetails> findByUserInfoAndSymbolAndIsExpiredFalse(UserInfo userInfo, String symbol);
	
	List<StockDetails> findByUserInfoUserIdAndIsExpiredFalse(String userId);

    @Modifying
    @Query("UPDATE StockDetails s SET s.isExpired = true, s.expiredDate = :expiredDate " +
           "WHERE s.userInfo.userId = :userId AND s.symbol = :symbol AND s.isExpired = false")
    int expireActiveStocks(@Param("userId") String userId,
                           @Param("symbol") String symbol,
                           @Param("expiredDate") LocalDate expiredDate);	
}
