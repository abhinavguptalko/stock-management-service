package com.stock.management.external.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.management.exception.NoDataFoundForSymbolException;
import com.stock.management.external.service.StockFetchingStrategy;

@Service
public class AlphaVantageStockFetchingStrategy implements StockFetchingStrategy {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlphaVantageStockFetchingStrategy.class);

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public Double getStockPrice(String symbol) {
		String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + "demo";
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);

		// Check if the response is null or doesn't contain the expected data
		if (response == null || response.get("Global Quote") == null) {
			LOGGER.error("No Data found for Symbol {}", symbol);
			throw new NoDataFoundForSymbolException(symbol);
		}

		// Cast the response to get the quote details
		Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
		return Double.parseDouble(quote.get("05. price"));
	}
}
