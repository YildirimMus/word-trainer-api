# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run locally (requires MongoDB)
./mvnw spring-boot:run

# Run with Docker Compose (includes MongoDB)
docker-compose up -d

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

## Access Points

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/api/health`

## Architecture

Spring Boot 3.2 REST API (Java 21) with MongoDB and JWT authentication. Layered architecture: Controller → Service → Repository → Model.

**Package structure** (`com.wordtrainer`):
- `controller/` — REST endpoints under `/api`. Auth, Child, List, Training, Health controllers.
- `service/` — Business logic. AuthService, ChildService, ListService, TrainingService.
- `repository/` — MongoDB repositories extending `MongoRepository<T, String>`.
- `model/` — MongoDB documents: Parent, Child, WordList, TrainingSession. Use `@Document` with `@CreatedDate`/`@LastModifiedDate` for automatic timestamps.
- `dto/request/` and `dto/response/` — Request/response DTOs. All API responses wrapped in `ApiResponse<T>`.
- `security/` — `JwtTokenProvider` (token generation/validation with JJWT 0.12.5) and `JwtAuthenticationFilter` (extracts Bearer token from Authorization header).
- `config/` — SecurityConfig (CORS, public/protected endpoints), MongoConfig (auditing), OpenApiConfig (Swagger).
- `exception/` — Custom exceptions (`ResourceNotFoundException`, `UnauthorizedException`, `UsernameAlreadyExistsException`) handled by `GlobalExceptionHandler` returning `ApiResponse` with error codes.

## Key Patterns

- **Two user roles**: PARENT and CHILD, both use JWT auth but with different login endpoints (`/api/auth/login` vs `/api/auth/login/child`).
- **Ownership validation**: Services check parent-child relationships before allowing operations. Parents can manage their children; children can only access their own data.
- **Cascading deletes**: Deleting a child removes its word lists and training sessions. Deleting a word list removes its training sessions.
- **Environment variables**: `MONGODB_URI`, `JWT_SECRET`, `PORT`, `CORS_ORIGINS` — configured in `application.yml` with `${}` placeholders.
- **Lombok**: All models and DTOs use Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`). Configured as annotation processor in `pom.xml`.
- **Validation**: Request DTOs use Jakarta validation annotations (`@NotBlank`, `@Email`, `@Size`, `@NotEmpty`). Validation errors return field-level error maps.

## Public Endpoints (no auth required)

`/api/auth/**`, `/api/health`, `/api/check-username/**`, `/swagger-ui/**`, `/v3/api-docs/**`, all `OPTIONS` requests.

## No Tests Yet

The test directory has no implementations. Test dependencies are configured: JUnit 5, Mockito, AssertJ, spring-security-test.