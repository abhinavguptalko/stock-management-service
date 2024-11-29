---

# **Stock Management Application**

## **Overview**

The Stock Management Application is a Spring Boot-based application designed to manage user portfolios efficiently.
It provides features such as adding and removing stocks, calculating portfolio values, and maintaining user details.
The application integrates with external stock pricing APIs for real-time stock valuation and includes robust exception handling, logging, and performance monitoring.

---

## **Features**

### User Management
- Create users with unique alphanumeric user IDs.
- Manage user details, including username and email.

### Stock Management
- Add stocks to a user’s portfolio with real-time price fetching from an external API.
- Remove stocks from the portfolio.
- Retrieve all stocks owned by a user.
- Calculate the total portfolio value based on current stock prices.

### Additional Highlights
- **Performance Monitoring**: Tracks method execution time using Spring AOP.
- **Logging**: Comprehensive logs with Logback for debugging and operational insights.
- **Exception Handling**: Structured error responses using Spring’s `ProblemDetails`.

---

## **Technologies Used**

- **Framework**: Spring Boot 3
- **Database**: H2 (in-memory database for development)
- **Persistence**: Spring Data JPA
- **API Integration**: AlphaVantage API for fetching real-time stock prices
- **AOP**: Spring AOP for performance monitoring
- **Logging**: Logback with console and file appenders
- **Build Tool**: Maven
- **Languages**: Java 17

---

## **Project Structure**

```plaintext
src/
├── main/
│   ├── java/com/stock/management/
│   │   ├── controller/         # REST Controllers for handling user and stock APIs
│   │   ├── data/jpa/model/     # JPA Entity classes for UserDetails and StockDetails
│   │   ├── data/jpa/repository # Repositories for database operations
│   │   ├── dto/                # Data Transfer Objects for requests and responses
│   │   ├── external/service/   # External API integration for stock prices
│   │   ├── service/            # Business logic for user and stock management
│   │   ├── aspect/             # AOP aspects for performance logging
│   ├── resources/
│   │   ├── logback-spring.xml  # Logback configuration
│   │   ├── application.yml     # Spring Boot configuration
├── test/                       # Unit and integration tests
```

---

## **Getting Started**

### Prerequisites
- Java 17
- Maven
- An IDE or editor of your choice (e.g., IntelliJ IDEA, Eclipse)

### Setup Instructions
1. **Clone the Repository**  
   ```bash
   git clone https://github.com/abhinavguptalko/stock-management.git
   cd stock-management
   ```

2. **Build the Project**  
   Use Maven to install dependencies and build the project:  
   ```bash
   mvn clean install
   ```

3. **Run the Application**  
   Start the application using:  
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**  
   - Swagger Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - H2 Database Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
     (Default credentials: `jdbc:h2:mem:testdb`, username: `sa`, no password)

---

## **API Endpoints**

### User Management
| Method | Endpoint               | Description               |
|--------|------------------------|---------------------------|
| POST   | `/api/users`           | Create a new user         |

### Stock Management
| Method | Endpoint                                   | Description                           |
|--------|-------------------------------------------|---------------------------------------|
| POST   | `/api/users/{userId}/stocks`              | Add a stock to the user's portfolio  |
| DELETE | `/api/users/{userId}/stocks/{stockId}`    | Remove a stock from the portfolio    |
| GET    | `/api/users/{userId}/stocks`             | Get all stocks for the user          |
| GET    | `/api/users/{userId}/stocks/portfolio/value` | Calculate total portfolio value      |

---

## **Logging Configuration**

Logging is configured using **Logback**. Logs are written both to the console and to rolling log files in the `logs/` directory.

- **Log levels**: Configured as `INFO` globally with `DEBUG` for specific packages.
- **Log file location**: `logs/application.log`.

---

## **Performance Monitoring**

Using Spring AOP, all methods in the `service` package are wrapped with an around advice that logs the method name and execution time.

Example log output:
```plaintext
2024-11-22 11:30:00 INFO  PerformanceLogger - Method addStock executed in 123ms
```

---

## **Exception Handling**

This application uses Spring’s `ProblemDetails` to provide structured error responses for API failures. Example response for a validation error:

```json
{
  "type": "https://example.com/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "User ID must be alphanumeric.",
  "instance": "/api/users"
}
```

---

## **Testing**

Unit tests and integration tests are provided for all major components. Run tests using:

```bash
mvn test
```

---

## **Future Enhancements**

- Add OAuth2-based authentication and authorization.
- Enhance external API integration with caching for frequently queried stock prices.
- Deploy to cloud platforms like AWS or Azure with CI/CD pipelines.

---
