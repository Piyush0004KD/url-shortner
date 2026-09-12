# URL Shortener

A backend URL Shortener built using **Spring Boot, PostgreSQL, Redis, and JPA**.

The application converts long URLs into short 6-character URLs and redirects users to the original URL. Redis is used as a cache to improve redirect performance, while expired URLs are automatically removed using scheduled cleanup.

## Features

- Generate short 6-character URLs
- Redirect short URLs to the original URL
- PostgreSQL database using Spring Data JPA
- Redis caching for faster URL lookups
- Redis cache expiration after 1 hour
- Automatic removal of expired URLs
- Maximum limit of 100 stored URLs
- Paginated URL listing
- Sorting and pagination support with Spring `Pageable`
- DTO-based request/response handling
- Swagger/OpenAPI documentation
- Unit testing with JUnit and Mockito

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.1**
- Spring Web MVC
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Lombok
- Springdoc OpenAPI
- JUnit 5
- Mockito
- Maven

## Project Structure

```text
src/main/java/com/PiyushKD/urlShortner
├── dto
│   ├── UrlMapper.java
│   ├── UrlRequest.java
│   └── UrlResponse.java
├── entity
│   └── Url.java
├── repository
│   └── UrlRepo.java
├── urlController
│   └── UrlController.java
├── urlService
│   └── UrlService.java
└── UrlShortnerApplication.java
```

## How It Works

### 1. Create a Short URL

A client sends the original URL to the API:

```http
POST /
```

The service:

1. Checks whether the URL limit has been reached.
2. Generates a random 6-character short code.
3. Stores the original URL, short code, and creation time in PostgreSQL.
4. Returns the generated short URL.

Example response:

```text
http://localhost:8080/ykNHk7
```

### 2. Redirect

When a user opens:

```http
GET /ykNHk7
```

The application first checks Redis.

- **Redis hit:** return the cached original URL.
- **Redis miss:** query PostgreSQL, store the URL in Redis, and return the original URL.

The controller responds with HTTP `302 FOUND` and the original URL in the `Location` header.

## Redis Caching

Redis is used to reduce repeated database lookups during redirects.

Flow:

```text
Client
  |
  v
GET /shortCode
  |
  v
Redis
  |
  +---- Cache Hit ----> Original URL
  |
  +---- Cache Miss
            |
            v
        PostgreSQL
            |
            v
        Store in Redis
            |
            v
        Original URL
```

Cached URLs are stored for **1 hour**.

## Database

The application uses PostgreSQL with Spring Data JPA.

The URL entity stores information such as:

- ID
- Short URL/code
- Original URL
- Creation timestamp

## Automatic URL Expiration

Expired URLs are automatically removed using Spring's scheduled task support.

The cleanup task runs every hour and checks the creation time of stored URLs. URLs that have existed for at least one hour are deleted from:

1. Redis
2. PostgreSQL

Scheduling is enabled using Spring's `@EnableScheduling`.

## Pagination

The URL listing endpoint supports Spring Data pagination and sorting.

Example:

```http
GET /?page=0&size=25
```

With sorting:

```http
GET /?page=0&size=10&sort=createdAt,desc
```

Pagination is handled using Spring's `Pageable` and `Page`.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/` | Create a short URL |
| GET | `/{shortUrl}` | Redirect to original URL |
| GET | `/` | Get paginated list of stored URLs |

## Swagger / OpenAPI

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

It can be used to view and test the available APIs.

## Testing

The project contains unit tests for the service layer using **JUnit 5 and Mockito**.

Tests cover scenarios including:

- Successful URL creation
- URL limit handling
- URL deletion
- Expired URL cleanup
- Redis cache hit
- Redis cache miss
- URL not found
- Pagination

Run the tests with:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

The service tests mock external dependencies such as PostgreSQL repositories and Redis, so a real Redis server is not required for those unit tests.

## Prerequisites

Before running the application, make sure you have:

- Java 17 or compatible JDK
- PostgreSQL
- Redis
- Maven (optional because the project includes Maven Wrapper)

Check Redis with:

```bash
redis-cli ping
```

Expected output:

```text
PONG
```

## Configuration

Configure your PostgreSQL and Redis connection properties in `application.properties`.

Example Redis configuration:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

Do **not** commit database passwords, API keys, or other secrets to GitHub.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Piyush0004KD/url-shortner.git
cd url-shortner
```

Start the application using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

## Example Usage

### Create a Short URL

Using `curl`:

```bash
curl -X POST http://localhost:8080/ \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://www.google.com/"}'
```

Example response:

```text
http://localhost:8080/abcd12
```

### Open the Short URL

```bash
curl -i http://localhost:8080/abcd12
```

The application responds with a redirect to the original URL.

### Get Stored URLs

```bash
curl "http://localhost:8080/?page=0&size=10"
```

## Current Limitations

- Short codes are generated randomly and collision handling is not currently implemented.
- URL validation can be improved with stronger validation rules.
- Generic `RuntimeException` is currently used for some error cases.
- The maximum URL limit is fixed at 100.
- Authentication and authorization are not implemented.
- `Math.random()` is used for short-code generation.

## Future Improvements

Possible improvements include:

- Add collision detection and retry logic for generated short codes.
- Add custom exception classes and global exception handling.
- Add stronger URL validation.
- Add controller/API integration tests.
- Add authentication and authorization.
- Make the URL limit configurable.
- Improve short-code generation.
- Add analytics such as click counts.
- Add Docker support for PostgreSQL and Redis.
- Add CI/CD using GitHub Actions.

## Author

**Piyush**

GitHub: https://github.com/Piyush0004KD
