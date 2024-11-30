Feature: Stock Management

  Scenario: Add Stock to Portfolio
    Given the user with ID "user123" exists
    When the user adds 10 shares of AAPL stock
    Then the stock should be added to the user's portfolio

  Scenario: Remove Stock from Portfolio
    Given the user with ID "user123" has AAPL stock
    When the user removes AAPL stock from their portfolio
    Then the stock should be removed from the user's portfolio

  Scenario: Get All Stocks for a User
    Given the user with ID "user123" has some stocks in their portfolio
    When the user requests their stock portfolio
    Then the response should contain a list of stocks

  Scenario: Calculate Portfolio Value
    Given the user with ID "user123" has some stocks in their portfolio
    When the user requests their portfolio value
    Then the response should return the total portfolio value
