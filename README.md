<img width="1913" height="968" alt="image" src="https://github.com/user-attachments/assets/03e8160a-96d0-476c-8fab-9f7344461788" />

# URL Shortener

A backend URL Shortener built using **Spring Boot, PostgreSQL, Redis, and JPA**, paired with a **React + Vite** frontend.

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
- CORS-enabled API for local frontend development

## Tech Stack

### Backend

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

### Frontend

- **React 19**
- **Vite**
- JavaScript
- HTML
- CSS
- Fetch API

## Project Structure

```text
url-shortner
│
├── src/main/java/com/PiyushKD/urlShortner
│   ├── dto
│   │   ├── UrlMapper.java
│   │   ├── UrlRequest.java
│   │   └── UrlResponse.java
│   ├── entity
│   │   └── Url.java
│   ├── repository
│   │   └── UrlRepo.java
│   ├── urlController
│   │   └── UrlController.java
│   ├── urlService
│   │   └── UrlService.java
│   └── UrlShortnerApplication.java
│
└── urlShortner-frontend
    ├── src
    │   ├── assets
    │   ├── App.jsx
    │   ├── Card.jsx
    │   ├── Card.css
    │   ├── UrlList.jsx
    │   ├── UrlList.css
    │   ├── index.css
    │   └── main.jsx
    ├── public
    ├── package.json
    ├── vite.config.js
    └── index.html
```

## API Endpoints

| Method | Endpoint         | Description                                             |
|--------|------------------|----------------------------------------------------------|
| POST   | `/`              | Create a short URL. Body: `{ "originalUrl": "https://..." }` |
| GET    | `/{shortUrl}`    | Redirects (`302 FOUND`) to the original long URL          |
| GET    | `/`              | Get a paginated list of all stored URLs (`?page=&size=&sort=`) |

Swagger UI is available once the backend is running at:
`http://localhost:8080/swagger-ui/index.html`

## Prerequisites

Make sure you have the following installed:

- Java 17+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- Node.js 18+ and npm
- PostgreSQL (running locally or in a container)
- Redis (running locally or in a container)

Quick start for PostgreSQL and Redis using Docker:

```bash
docker run --name urlshortner-postgres -e POSTGRES_PASSWORD=yourpassword \
  -e POSTGRES_DB=urlshortner -p 5432:5432 -d postgres

docker run --name urlshortner-redis -p 6379:6379 -d redis
```

## Backend Setup

1. Clone the repository and move into the backend folder:

   ```bash
   git clone <repo-url>
   cd urlShortner
   ```

2. Configure your database and Redis connection in
   `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortner
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword

   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   ```

   > It's recommended to move credentials out of `application.properties` and into environment variables or a `.env`/`application-local.properties` file that is excluded from version control.

3. Build and run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

   The backend will start on `http://localhost:8080`.

4. Run the tests:

   ```bash
   ./mvnw test
   ```

## Frontend Setup

1. Move into the frontend folder:

   ```bash
   cd urlShortner-frontend
   ```

2. Install dependencies:

   ```bash
   npm install
   ```

3. Start the development server:

   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173` and communicate with the backend at `http://localhost:8080` (CORS is already configured on the backend for this origin).

4. Build for production:

   ```bash
   npm run build
   ```

## How It Works

1. The user submits a long URL from the React frontend.
2. The backend generates a random 6-character alphanumeric code and stores the mapping (`shortUrl` → `longUrl`) in PostgreSQL.
3. On visiting the short URL, the backend first checks Redis for a cached mapping; if found, it redirects immediately.
4. If not cached, it looks up PostgreSQL, redirects the user, and caches the result in Redis for 1 hour.
5. A scheduled job runs hourly to remove URLs older than 1 hour from the database and Redis cache.

## Author

**Piyush KD**
