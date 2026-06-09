# SafetySpot – Copilot Instructions

## Project Overview

SafetySpot is a school safety awareness app. Students play through interactive hazard scenarios (chemistry lab, workshop, PE, etc.) and earn points. Teachers manage classes and create scenarios. Schools buy licenses.

**Two-component architecture:**
- `ssbackend/` — Spring Boot REST API (Java 26, Maven)
- Android app — Kotlin, Android Studio (not yet in this repo)

**Hosting:** Microsoft Azure — App Service for the API, Azure SQL for production database. MariaDB locally.

---

## Backend (`ssbackend/`)

### Commands

```bash
# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SsbackendApplicationTests

# Build
./mvnw package

# Run locally
./mvnw spring-boot:run
```

All Maven commands must be run from the `ssbackend/` directory.

### Stack

- **Spring Boot 4.0.6**, Java 26
- **Spring Data JPA** — persistence layer
- **Spring Security** — auth
- **Spring Shell** — admin CLI
- **MariaDB** (`mariadb-java-client`) — local dev database
- **Lombok** — excluded from the final jar; annotation processing configured for both compile and test phases in `pom.xml`

### Package structure

Base package: `spot.safety.ssbackend`

The project is early-stage. Grow the package tree under this base with standard Spring layering: `controller`, `service`, `repository`, `model`/`entity`, `dto`, `config`, `security`.

---

## Domain Model

Three user roles with different access levels:

| Role | Access |
|------|--------|
| **Student** (Schüler) | Login, play scenarios, view own score/ranking |
| **Teacher** (Lehrer) | + Manage classes/students, create scenarios, view class stats |
| **Admin** (School) | + License/org management |

Students register with **username + password only** (no email). Accounts belong to a school/class. Teachers can reset passwords.

**Core entities to implement:**

1. **Org & Roles** — School, Class, User (role enum: STUDENT / TEACHER / ADMIN), hashed passwords
2. **Content** — Category, Scenario (title, description, difficulty, version, status), Task (question, answer options, correct answer, feedback, points)
3. **Progress & Gamification** — StudentProgress (per student per scenario: best score, attempts, completed), Points/Level/Streak, Leaderboard
4. **Admin/License** — LicenseStatus per school, audit log entries

---

## Key Conventions

- **TDD** — the project specification explicitly calls for Test Driven Development. Write tests before or alongside implementation.
- **Null-safety** — this was the primary reason Kotlin was chosen for the Android side; apply the same discipline in Java (use `Optional`, `@NonNull`/`@Nullable` from Lombok or JSpecify, never return null from public methods).
- **Passwords are always stored hashed** — never plaintext, not even in tests.
- **Data lives on the server** — the Android app is a thin client; the backend is the single source of truth. The app may cache scenarios locally and sync pending attempts when back online.
- **REST API** — the backend exposes a REST API consumed by the Android app. Use versioned URL paths (e.g. `/api/v1/…`).
- Lombok is configured for annotation processing — use `@Data`, `@Builder`, `@NoArgsConstructor`/`@RequiredArgsConstructor` on entities and DTOs, but be careful with `@Data` on JPA entities (avoid bi-directional `equals`/`hashCode` cycles).

---

## Documentation

Design and architecture docs live in `docs/`:
- `docs/v0/` — initial project definition (German), information model, architecture decisions
- `docs/v1/` — class diagram (`class-diagram.png`)
