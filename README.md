# Rating System API

This is a Spring Boot REST API for managing user ratings, comments, game objects, and authentication with JWT.

## Features

-   **User Management:** Registration with email confirmation, login, password reset.
-   **Comments & Ratings:** Submit comments on sellers, admin approval, seller rating system.
-   **Game Objects:** Create and retrieve game objects (seller role required).
-   **Authentication:** JWT-based authentication and role-based access control.
-   **Redis Integration:** Password reset tokens stored in Redis.
-   **Testing:** Comprehensive integration tests.

## Technologies Used

-   **Java:** 17
-   **Spring Boot:** 3.4.3
-   **Spring Security:** JWT-based authentication.
-   **Spring Data JPA**
-   **Spring Data Redis**
-   **Spring Web MVC**
-   **SpringDoc OpenAPI**
-   **Spring Boot Mail**
-   **PostgreSQL**
-   **Redis (Lettuce Core)**
-   **Lombok**
-   **Hibernate Validator**
-   **JUnit 5**

## Gradle Plugins

-   `java`
-   `org.springframework.boot` version `3.4.3`
-   `io.spring.dependency-management` version `1.1.7`

## Getting Started

### Prerequisites

-   Java 17
-   PostgreSQL
-   Redis

### Installation

1.  Clone the repository: `git clone https://github.com/beqaperanidze/prj-rating-system`
2.  Build the project: `./gradlew build`
3.  Configure `application.properties` or `application.yml` with your database, Redis, and JWT secret key.

### Running the Application

-   Run: `./gradlew bootRun`

## API Endpoints

-   `/api/auth`: User authentication and password management with JWT.
-   `/api/comments`: Comment submission and retrieval.
-   `/api/game-objects`: Game object creation and retrieval.
-   `/api/admin`: Administrative actions (comment approval).

## Testing

-   Run integration tests: `./gradlew test -Dspring.profiles.active=test`

