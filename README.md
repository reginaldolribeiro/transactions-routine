# Transactions API

## Description 📝
A REST API to manage accounts and transactions for financial operations.

## Approach

### Architecture 🏛️
The application is developed with a clean, layered architecture designed to be simple for an MVP while being extensible for future enhancements. The architecture follows these layers:

- **Controller Layer**: Handles HTTP requests and responses, input validation
- **Service Layer**: Contains business logic and data transformation using DTOs
- **Repository Layer**: Data access layer using Spring Data JPA
- **Model Layer**: Domain entities representing core business objects
- **Configuration Layer**: Application configuration and Jackson serialization

For future improvements, it would be beneficial to apply the Hexagonal Architecture (Ports & Adapters) pattern more rigorously, aligning with Clean Architecture principles to enhance decoupling, testability, and maintainability. This can be achieved by introducing features such as:
- **Explicit Use Cases instead of a single Service**: Define dedicated Use Case classes that encapsulate and orchestrate business logic (e.g., `CreateTransactionUseCase`, `CreateAccountUseCase`, `FindAccountByIdUseCase`).
- **Input/Output DTOs in Use Cases**: Enforce separation of concerns by defining clear input and output boundaries between the application layer and external layers.
- **Clear Port Definitions**: Formalize interfaces (ports) for external dependencies (e.g., repositories, external services) and implement them through infrastructure-specific adapters.


## Endpoints 🚀

### Accounts
- **`POST /v1/accounts`**: Create a new account
- **`GET /v1/accounts/{id}`**: Retrieve account information by ID

### Transactions
- **`POST /v1/transactions`**: Create a new transaction with types such as Normal Purchase, Purchase with Installments, Withdrawal, and Credit Voucher
- **`GET /v1/transactions/{id}`**: Retrieve transaction details by ID

## How to Run the Project 💻

### Prerequisites ✅
- **Docker** and **Docker Compose**: [Install Docker](https://docs.docker.com/get-docker/)

### Quickest Way (Recommended) ⚡

This project is configured to run in a self-contained Docker environment. With the provided script, you can build, test, and run the application with simple commands.

1.  **Start the Application**:
    ```bash
    ./run.sh
    ```
    This is the default command. It will build the app's Docker image, start the database and application containers, and then tail the application logs.

2.  **View Application Logs**:
    ```bash
    ./run.sh logs
    ```
    This command tails the application logs, which is useful for debugging and monitoring.

3.  **Run Tests**:
    ```bash
    ./run.sh test
    ```
    This command will execute all unit and integration tests inside a Docker container, ensuring a consistent test environment.

4.  **Stop the Application**:
    ```bash
    ./run.sh down
    ```
    This stops and removes all running containers.

### Alternative: Local Maven Build

If you prefer to run the application directly on your host machine, you can use the Maven wrapper.

**Prerequisites for this method:**
- **Java 21** or higher
- **Docker** (for the database)

**Steps:**

1.  **Build the Project**:
    ```bash
    ./mvnw clean install
    ```

2.  **Start the Database**:
    ```bash
    docker compose up -d db
    ```

3.  **Run the Application**:
    ```bash
    ./mvnw spring-boot:run
    ```

### Accessing the API 🌐

Once the application is running (using either method), you can access the API:
- **Swagger UI**: [http://localhost:8080/transactions-api/swagger-ui.html](http://localhost:8080/transactions-api/swagger-ui.html)

### Test Data Available

The database includes seed data for quick API testing:

**Test Account:**
- **ID**: 1
- **Document Number**: 12345678900

**Operation Types:**
1. Normal Purchase (ID: 1)
2. Purchase with installments (ID: 2)
3. Withdrawal (ID: 3)
4. Credit Voucher (ID: 4)

**Sample Transactions:**
- Account 1 has several transactions including purchases and a credit voucher

## Try the API

### Quick Start with Swagger UI 🚀

The easiest way to explore and test the API is through Swagger UI:

1. **Open Swagger UI** in your browser:
   [http://localhost:8080/transactions-api/swagger-ui.html](http://localhost:8080/transactions-api/swagger-ui.html)

2. **Test Account Endpoints**:
    - Find the `GET /v1/accounts/{id}` endpoint
    - Click "Try it out"
    - Enter account ID: `1`
    - Click "Execute" to see the test account information

3. **Create a New Account**:
    - Find the `POST /v1/accounts` endpoint
    - Click "Try it out"
    - Use this sample data:
      ```json
      {
        "document_number": "98765432100"
      }
      ```
    - Click "Execute" to create a new account

4. **Create a Transaction**:
    - Find the `POST /v1/transactions` endpoint
    - Click "Try it out"
    - Use this sample data:
      ```json
      {
        "account_id": 1,
        "operation_type_id": 1,
        "amount": 123.45
      }
      ```
    - Click "Execute" to create a new transaction

5. **View Transaction Details**:
    - Find the `GET /v1/transactions/{id}` endpoint
    - Click "Try it out"
    - Enter a transaction ID (try ID 1 from seed data)
    - Click "Execute" to see transaction details

### Alternative: Using curl Commands

**Quick Test Commands:**
```bash
# 1. Get account information
curl -X GET http://localhost:8080/transactions-api/v1/accounts/1

# 2. Get transaction details
curl -X GET http://localhost:8080/transactions-api/v1/transactions/1
```

**Complete curl Examples:**

1. **Create a New Account**:
   ```bash
   curl -X POST http://localhost:8080/transactions-api/v1/accounts \
     -H "Content-Type: application/json" \
     -d '{
       "document_number": "98765432100"
     }'
   ```

2. **Get Account Information**:
   ```bash
   curl -X GET http://localhost:8080/transactions-api/v1/accounts/1
   ```

3. **Create a Transaction**:
   ```bash
   curl -X POST http://localhost:8080/transactions-api/v1/transactions \
     -H "Content-Type: application/json" \
     -d '{
       "account_id": 1,
       "operation_type_id": 1,
       "amount": 123.45
     }'
   ```

4. **Get Transaction Details**:
   ```bash
   curl -X GET http://localhost:8080/transactions-api/v1/transactions/1
   ```



## Technical Details 🛠️

### Language and Frameworks Used

- **[Java 21](https://openjdk.org/)** - Latest LTS version
- **[Spring Boot](https://spring.io/projects/spring-boot)** - Java framework for building web applications.
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** - Data access layer with Hibernate
- **[Spring Validation](https://docs.spring.io/spring-framework/reference/core/validation.html)** - Request validation
- **[Jackson](https://github.com/FasterXML/jackson)** - JSON serialization/deserialization
- **[JUnit 5](https://junit.org/junit5/)** - Unit and integration testing
- **[PostgreSQL](https://www.postgresql.org/)** - Production database
- **[SpringDoc OpenAPI](https://springdoc.org/)** - API documentation (Swagger)
- **[Flyway](https://flywaydb.org/)** - Database migration management
- **[Maven](https://maven.apache.org/)** - Build and dependency management

### Additional Commands

**Build Without Running Tests**:
```bash
# Skip tests to create a build faster
./mvnw clean install -DskipTests
```

**Run All Tests**:
```bash
./mvnw test
```

**Run a Specific Test Class**:
```bash
# Replace 'AccountControllerTest' with the name of the test class you want to run
./mvnw test -Dtest=AccountControllerTest
```

**View Application Logs**:
```bash
# Follow the application logs in real-time
docker compose logs -f app
```

**Access and Query the Database**:
```bash
# Open a psql shell to the database container
docker compose exec db psql -U postgres -d transactions_db

# Inside the psql shell, you can run these commands:

# List all tables
\dt

# View accounts
SELECT * FROM accounts;

# View transactions
SELECT * FROM transactions;

# Exit the shell
\q
```

**Stop the Database and Remove Volumes**:
```bash
# Warning: This command will stop the database container and permanently delete its data volume.
docker compose down --volumes
```

---