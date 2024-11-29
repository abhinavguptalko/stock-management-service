package com.stock.management.external.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.management.external.service.StockFetchingStrategy;

@Service
public class AlphaVantageStockFetchingStrategy implements StockFetchingStrategy{

	 @Autowired
	    private RestTemplate restTemplate;
	 
	
	
	@Override
    public Double getStockPrice(String symbol) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" 
                     + symbol + "&apikey=" + "demo";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
        return Double.parseDouble(quote.get("05. price"));
    }

}

