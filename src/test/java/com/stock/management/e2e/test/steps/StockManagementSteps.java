package com.stock.management.e2e.test.steps;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import com.stock.management.dto.StockDTO;
import com.stock.management.dto.UserStockDTO;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StockManagementSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private StockDTO stockDTO;
    private UserStockDTO userStockDTO;
    private ResponseEntity<StockDTO> response;

    @Given("I have a stock DTO for stock {string} with {int} quantity")
    public void iHaveAStockDTOForStockWithQuantity(String symbol, int quantity) {
        stockDTO = new StockDTO(symbol, quantity);
    }

    @When("I post the stock DTO to add the stock")
    public void iPostTheStockDTOToAddTheStock() {
        response = restTemplate.postForEntity(
            "/api/users/1/stocks", 
            stockDTO, 
            StockDTO.class);  // Correct response class
    }

    @Then("The response should be a 204 No Content")
    public void theResponseShouldBeNoContent() {
        assertThat(response.getStatusCodeValue()).isEqualTo(204);
    }

    @When("I get the user's stock portfolio")
    public void iGetTheUsersStockPortfolio() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/users/1/stocks", 
            String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }
}
