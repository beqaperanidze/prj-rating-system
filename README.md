# Rating System API

This is a Spring Boot REST API for managing user ratings, comments, game objects, and authentication.

## Features

-   **User Management:** Registration with email confirmation, login, password reset.
-   **Comments & Ratings:** Submit comments on sellers, admin approval, seller rating system.
-   **Game Objects:** Create and retrieve game objects (seller role required).
-   **Authentication:** Role-based access control (RBAC).
-   **Redis Integration:** Password reset tokens stored in Redis.
-   **Testing:** Comprehensive integration tests.

## Technologies Used

-   **Java:** 23
-   **Spring Boot:** 3.4.3
-   **Spring Data JPA**
-   **Spring Data Redis**
-   **Spring Security**
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

-   Java 23
-   PostgreSQL
-   Redis

### Installation

1.  Clone the repository: `git clone https://github.com/beqaperanidze/prj-rating-system`
2.  Build the project: `./gradlew build`
3.  Configure `application.properties` or `application.yml` with your database and Redis connection details.

### Running the Application

-   Run: `./gradlew bootRun`

## API Endpoints

-   `/api/auth`: User authentication and password management.
-   `/api/comments`: Comment submission and retrieval.
-   `/api/game-objects`: Game object creation and retrieval.
-   `/api/admin`: Administrative actions (comment approval).

## Testing

-   Run integration tests: `./gradlew test -Dspring.profiles.active=test`

