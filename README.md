
# **Stock Management Application**

## **Overview**

The Stock Management Application is a Spring Boot-based backend designed to manage user portfolios efficiently.  
It offers features such as user registration and login, managing stock portfolios, calculating portfolio values, and tracking stock history.  
The application integrates with Spring Security for authentication, Spring 3 Problem Details for error handling, and includes performance monitoring and logging.

---

## **Features**

### User Management
- User registration and login using secure authentication.
- Password encryption with **BCryptPasswordEncoder**.

### Stock Management
- Add stocks to a user’s portfolio with quantity and purchase price.
- Remove stocks from the portfolio.
- Retrieve a list of all stocks owned by a user.
- Calculate total portfolio value dynamically based on stock prices.

### Stock History
- Track stock additions and removals with details such as **action** (Added/Removed), **symbol**, **quantity**, and **date**.
- Provides users with a historical record of their stock transactions, useful for auditing and tracking portfolio changes over time.

### Error Handling
- Leverages **Spring 3 Problem Details** for standardized error responses.

### Performance Monitoring
- Tracks execution time of critical methods using **Spring AOP**.
- Provides detailed logs of method performance.

### Logging
- Logging is configured using **Logback**, with logs written to both the console and rotating files.
- Separate log configurations for different environments (development, production).

---

## **Technologies Used**

- **Framework**: Spring Boot 3
  - Spring MVC for REST APIs.
  - Spring Security for authentication and authorization.
  - Spring Data JPA for persistence.
  - Spring AOP for performance monitoring.
- **Database**: H2 (in-memory for development).
- **Logging**: Logback for logging and debugging.
- **Languages**: Java 17.
- **Testing**: JUnit 5 and Mockito.
- **Build Tool**: Maven.

---

## **Project Structure**

```plaintext
src/
├── main/
│   ├── java/com/stock/management/
│   │   ├── controller/         # REST Controllers for APIs
│   │   ├── data/jpa/model/     # JPA Entity classes
│   │   ├── data/jpa/repository # Repositories for DB operations
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── service/            # Business logic for users and stocks
│   │   ├── aspect/             # AOP aspects for performance monitoring
│   │   └── exception/          # Exception handling classes
│   ├── resources/
│   │   ├── logback-spring.xml  # Logback configuration
│   │   ├── application.yml     # Application configuration
├── test/                       # Unit and integration tests
```

---

## **Getting Started**

### Prerequisites
- Java 17
- Maven
- IDE or text editor (e.g., IntelliJ IDEA, Eclipse)

### Setup Instructions
1. **Clone the Repository**  
   ```bash
   git clone https://github.com/abhinavguptalko/stock-management-service.git
   cd stock-management-service
   ```

2. **Build the Project**  
   ```bash
   mvn clean install
   ```

3. **Run the Application**  
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**  
   - Swagger Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - H2 Database Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
     (Default credentials: `jdbc:h2:mem:testdb`, username: `sa`, password: none)

---

## **API Endpoints**

### Authentication
| Method | Endpoint         | Description        |
|--------|------------------|--------------------|
| POST   | `/api/users/addUser`  | Register a new user |
| POST   | `/api/users/login`     | Authenticate |

### Stock Management
| Method | Endpoint                                   | Description                              |
|--------|-------------------------------------------|------------------------------------------|
| POST   | `/api/users/{userId}/stocks`              | Add stocks to the user's portfolio      |
| PUT    | `/api/users/{userId}/stocks/removeStock`  | Remove stocks from the portfolio        |
| GET    | `/api/users/{userId}/stocks`             | Get all stocks for the user             |
| GET    | `/api/users/{userId}/stocks/portfolio/value` | Calculate total portfolio value          |

### Stock History
| Method | Endpoint                   | Description                                 |
|--------|----------------------------|---------------------------------------------|
| GET    | `/api/stock-history/{userId}` | Retrieve the stock history of the user     |

---

## **Stock History**

The **Stock History** feature allows users to track all actions related to their stock portfolio. This includes any **stock additions** and **removals**, along with important details such as:

- **Stock Symbol**: The symbol of the stock.
- **Action**: Whether the stock was added or removed.
- **Quantity**: The number of stocks added or removed.
- **Date**: The date the action occurred.

### Stock History API

| Method | Endpoint                   | Description                                 |
|--------|----------------------------|---------------------------------------------|
| GET    | `/api/stock-history/{userId}` | Retrieve the stock history for the user    |

This endpoint will return a list of historical records for each stock, detailing every change made to the user's portfolio.

---

## **Logging Configuration**

Logging is configured using **Logback**, which supports both console and file-based logging.  
Logs include:
- Request details.
- Application performance metrics.
- Error stack traces.

- **Log Levels**:
  - Development: `DEBUG`.
  - Production: `INFO`.

---

## **Performance Monitoring**

Using Spring AOP, the application tracks the execution time of all methods in the `service` package.  
**Example Log**:
```plaintext
2024-11-22 11:30:00 INFO PerformanceAspect - Method addStock executed in 120ms.
```

---

## **Error Handling**

```plaintext
src/
├── main/
│   ├── java/com/stock/management/exception/
│   │   ├── CustomException.java       # A generic exception class for custom error handling
│   │   ├── StockNotFoundException.java # Exception thrown when a stock is not found
│   │   ├── UserNotFoundException.java  # Exception thrown when a user is not found
│   │   ├── GlobalExceptionHandler.java # Class to handle exceptions globally and return ProblemDetails
```

---

The application uses **Spring 3 Problem Details** for consistent error responses.  
**Example Response**:
```json
{
  "type": "https://example.com/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "The stock symbol is required.",
  "instance": "/api/users/1/stocks"
}
```

---

## **Testing**

Run tests using:
```bash
mvn test
```
Tests include:
- Unit tests for services and controllers.
- Integration tests for API endpoints.

---

## **Future Enhancements**

- Implement OAuth2 for enhanced authentication and authorization.
- Add caching for frequently queried stock prices.
- Deploy to cloud platforms like AWS or Azure with CI/CD pipelines.

---
