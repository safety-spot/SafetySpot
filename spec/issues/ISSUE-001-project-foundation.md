# ISSUE-001 — Project Foundation

**Epic:** Foundation  
**Labels:** `backend`, `foundation`  
**Depends on:** —  
**Blocks:** All other issues

---

## Summary

Set up the structural skeleton of the Spring Boot project: package layout, global exception
handling, base `application.yml` configs, and a passing health-check test. This is the shared
foundation everyone else builds on — it must be merged before any other issue starts.

---

## Acceptance Criteria

- [ ] Package tree matches the structure in `spec/plan.md §1` (empty placeholder classes are fine)
- [ ] `application.yml` (dev) is present and configures MariaDB datasource via env vars `DB_URL`, `DB_USER`, `DB_PASSWORD`
- [ ] `application-prod.yml` is present and configures Azure SQL Server datasource via the same env var names
- [ ] `GlobalExceptionHandler` (`@RestControllerAdvice`) handles at minimum: `EntityNotFoundException → 404`, `AccessDeniedException → 403`, `MethodArgumentNotValidException → 400`
- [ ] Every error response uses the shared shape: `{ status, error, message, timestamp }`
- [ ] `SsbackendApplicationTests` context-load test passes (`./mvnw test`)

---

## Technical Details

### Files to create

**Config:**
```
src/main/resources/application.yml
src/main/resources/application-prod.yml
```

**Exception classes** (`config/exception/`):
- `EntityNotFoundException extends RuntimeException`
- `AccessDeniedException extends RuntimeException`
- `UsernameAlreadyTakenException extends RuntimeException`
- `ScenarioNotEditableException extends RuntimeException`  ← keep for later, just define
- `DuplicateAttemptException extends RuntimeException`  ← keep for later, just define

**Handler:**
```
config/GlobalExceptionHandler.java   (@RestControllerAdvice)
```

**Error DTO:**
```java
// config/dto/ErrorResponse.java
record ErrorResponse(int status, String error, String message, Instant timestamp) {}
```

### `application.yml` (dev)

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mariadb://localhost:3306/safetyspot}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update        # switch to validate once migrations are added
    show-sql: false
  security:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  access-token-expiry: 3600
  refresh-token-expiry: 604800

logging:
  level:
    spot.safety.ssbackend: DEBUG
```

### `application-prod.yml`

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.SQLServerDialect
```

### GlobalExceptionHandler excerpt

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) { ... }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) { ... }

    // ... one handler per custom exception
}
```

---

## Out of Scope

- No Spring Security wiring yet (that is ISSUE-002)
- No entity classes yet (those are ISSUE-004)
- No actual database connection required for the test (use `spring.jpa.hibernate.ddl-auto=create-drop` + H2 in test scope if needed)
