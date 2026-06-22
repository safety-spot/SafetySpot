# SafetySpot – Backend Implementation Plan

> **Scope:** Spring Boot REST API (`ssbackend/`).  
> **Stack:** Spring Boot 4.0.6 · Java 26 · Spring Data JPA · Spring Security · Spring Shell · MariaDB (local) / Azure SQL (prod) · Lombok  
> **Base package:** `spot.safety.ssbackend`  
> **URL prefix:** `/api/v1`  
> **Auth:** JWT (stateless, Bearer token)

---

## 1. Package Structure

```
spot.safety.ssbackend
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── WebMvcConfig.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── UserPrincipal.java
├── model/
│   ├── School.java
│   ├── SchoolClass.java
│   ├── User.java
│   ├── Role.java                   (enum)
│   ├── Category.java
│   ├── Scenario.java
│   ├── ScenarioDifficulty.java     (enum)
│   ├── ScenarioStatus.java         (enum)
│   ├── Task.java
│   ├── TaskType.java               (enum)
│   ├── TaskOption.java
│   ├── StudentProgress.java
│   ├── AttemptResult.java
│   ├── AttemptAnswer.java
│   ├── UserPoints.java
│   ├── AuditLog.java
│   └── AuditAction.java            (enum)
├── dto/
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── RefreshTokenRequest.java
│   ├── user/
│   │   ├── CreateUserRequest.java
│   │   ├── UpdateUserRequest.java
│   │   ├── ResetPasswordRequest.java
│   │   └── UserResponse.java
│   ├── school/
│   │   ├── CreateSchoolRequest.java
│   │   ├── UpdateSchoolRequest.java
│   │   └── SchoolResponse.java
│   ├── schoolclass/
│   │   ├── CreateClassRequest.java
│   │   ├── UpdateClassRequest.java
│   │   └── ClassResponse.java
│   ├── category/
│   │   ├── CreateCategoryRequest.java
│   │   ├── UpdateCategoryRequest.java
│   │   └── CategoryResponse.java
│   ├── scenario/
│   │   ├── CreateScenarioRequest.java
│   │   ├── UpdateScenarioRequest.java
│   │   └── ScenarioResponse.java
│   ├── task/
│   │   ├── CreateTaskRequest.java
│   │   ├── UpdateTaskRequest.java
│   │   ├── ReorderTasksRequest.java
│   │   └── TaskResponse.java
│   ├── attempt/
│   │   ├── StartAttemptRequest.java
│   │   ├── SubmitAnswersRequest.java
│   │   ├── AnswerItem.java
│   │   └── AttemptResponse.java
│   ├── progress/
│   │   ├── ProgressResponse.java
│   │   └── ScenarioProgressResponse.java
│   ├── leaderboard/
│   │   ├── LeaderboardEntryResponse.java
│   │   └── LeaderboardResponse.java
│   └── stats/
│       ├── ClassStatsResponse.java
│       ├── ScenarioStatsResponse.java
│       └── StudentStatsResponse.java
├── repository/
│   ├── SchoolRepository.java
│   ├── SchoolClassRepository.java
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   ├── ScenarioRepository.java
│   ├── TaskRepository.java
│   ├── TaskOptionRepository.java
│   ├── StudentProgressRepository.java
│   ├── AttemptResultRepository.java
│   ├── AttemptAnswerRepository.java
│   ├── UserPointsRepository.java
│   └── AuditLogRepository.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── SchoolService.java
│   ├── SchoolClassService.java
│   ├── CategoryService.java
│   ├── ScenarioService.java
│   ├── TaskService.java
│   ├── AttemptService.java
│   ├── ProgressService.java
│   ├── LeaderboardService.java
│   ├── StatsService.java
│   ├── PointsService.java
│   └── AuditService.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── SchoolController.java
│   ├── SchoolClassController.java
│   ├── CategoryController.java
│   ├── ScenarioController.java
│   ├── TaskController.java
│   ├── AttemptController.java
│   ├── ProgressController.java
│   ├── LeaderboardController.java
│   └── StatsController.java
├── shell/
│   └── AdminShell.java
└── SsbackendApplication.java
```

---

## 2. Domain Model (Entities)

All entities: `@Entity`, `@Table`, Lombok `@Data` (without `@EqualsAndHashCode` on JPA relations), `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.

---

### 2.1 `School`

```
Table: schools
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `name` | `String` | NOT NULL, unique | School display name |
| `licenseStatus` | `LicenseStatus` (enum) | NOT NULL, default INACTIVE | INACTIVE / ACTIVE / EXPIRED |
| `licenseExpiry` | `LocalDate` | nullable | null = no active license |
| `createdAt` | `Instant` | NOT NULL | set on persist |
| `updatedAt` | `Instant` | NOT NULL | set on update |

Relations: one-to-many → `SchoolClass`, one-to-many → `User`

---

### 2.2 `SchoolClass`

```
Table: classes
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `name` | `String` | NOT NULL | e.g. "10a" |
| `school` | `School` | FK NOT NULL | ManyToOne |
| `teacher` | `User` | FK nullable | ManyToOne; null allowed (class without teacher yet) |
| `createdAt` | `Instant` | NOT NULL | |

Relations: one-to-many → `User` (students)

---

### 2.3 `User`

```
Table: users
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `username` | `String` | NOT NULL, unique | no email required |
| `passwordHash` | `String` | NOT NULL | bcrypt |
| `role` | `Role` (enum) | NOT NULL | STUDENT / TEACHER / ADMIN |
| `school` | `School` | FK NOT NULL | ManyToOne |
| `schoolClass` | `SchoolClass` | FK nullable | ManyToOne; null for TEACHER/ADMIN |
| `active` | `boolean` | NOT NULL, default true | soft-disable |
| `createdAt` | `Instant` | NOT NULL | |
| `lastLoginAt` | `Instant` | nullable | updated on login |

---

### 2.4 `Role` (enum)

Values: `STUDENT`, `TEACHER`, `ADMIN`

---

### 2.5 `LicenseStatus` (enum)

Values: `INACTIVE`, `ACTIVE`, `EXPIRED`

---

### 2.6 `Category`

```
Table: categories
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `name` | `String` | NOT NULL, unique | e.g. "Chemie", "Werkraum" |
| `description` | `String` | nullable | |
| `iconUrl` | `String` | nullable | URL to icon asset |
| `createdAt` | `Instant` | NOT NULL | |

---

### 2.7 `Scenario`

```
Table: scenarios
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `title` | `String` | NOT NULL | |
| `description` | `String` | nullable | |
| `difficulty` | `ScenarioDifficulty` (enum) | NOT NULL | EASY / MEDIUM / HARD |
| `status` | `ScenarioStatus` (enum) | NOT NULL, default DRAFT | DRAFT / PUBLISHED / ARCHIVED |
| `version` | `int` | NOT NULL, default 1 | incremented on publish |
| `category` | `Category` | FK NOT NULL | ManyToOne |
| `createdBy` | `User` | FK NOT NULL | ManyToOne (Teacher who created it) |
| `createdAt` | `Instant` | NOT NULL | |
| `updatedAt` | `Instant` | NOT NULL | |

Relations: one-to-many → `Task` (ordered)

---

### 2.8 `ScenarioDifficulty` (enum)

Values: `EASY`, `MEDIUM`, `HARD`

---

### 2.9 `ScenarioStatus` (enum)

Values: `DRAFT`, `PUBLISHED`, `ARCHIVED`

---

### 2.10 `Task`

```
Table: tasks
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `scenario` | `Scenario` | FK NOT NULL | ManyToOne |
| `orderIndex` | `int` | NOT NULL | position within scenario |
| `questionText` | `String` | NOT NULL | |
| `type` | `TaskType` (enum) | NOT NULL | SINGLE_CHOICE / MULTI_CHOICE / TRUE_FALSE |
| `points` | `int` | NOT NULL, ≥ 1 | max points for correct answer |
| `feedbackCorrect` | `String` | NOT NULL | shown after correct answer |
| `feedbackWrong` | `String` | NOT NULL | shown after wrong answer |

Relations: one-to-many → `TaskOption`

---

### 2.11 `TaskType` (enum)

Values: `SINGLE_CHOICE`, `MULTI_CHOICE`, `TRUE_FALSE`

---

### 2.12 `TaskOption`

```
Table: task_options
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `task` | `Task` | FK NOT NULL | ManyToOne |
| `text` | `String` | NOT NULL | answer option text |
| `isCorrect` | `boolean` | NOT NULL | whether this option is a correct answer |
| `orderIndex` | `int` | NOT NULL | display order |

---

### 2.13 `StudentProgress`

Tracks the best result per student per scenario. One row per (student, scenario) pair.

```
Table: student_progress
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `student` | `User` | FK NOT NULL | ManyToOne |
| `scenario` | `Scenario` | FK NOT NULL | ManyToOne |
| `attempts` | `int` | NOT NULL, default 0 | total number of plays |
| `bestScore` | `int` | NOT NULL, default 0 | highest score achieved |
| `completed` | `boolean` | NOT NULL, default false | true when score ≥ 100% |
| `lastAttemptAt` | `Instant` | nullable | |

Unique constraint: `(student_id, scenario_id)`

---

### 2.14 `AttemptResult`

One row per full play-through of a scenario.

```
Table: attempt_results
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `student` | `User` | FK NOT NULL | ManyToOne |
| `scenario` | `Scenario` | FK NOT NULL | ManyToOne |
| `scoreEarned` | `int` | NOT NULL, default 0 | |
| `scoreMax` | `int` | NOT NULL | total possible points |
| `startedAt` | `Instant` | NOT NULL | |
| `completedAt` | `Instant` | nullable | null = in progress |

Relations: one-to-many → `AttemptAnswer`

---

### 2.15 `AttemptAnswer`

One row per task answered within an attempt.

```
Table: attempt_answers
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `attempt` | `AttemptResult` | FK NOT NULL | ManyToOne |
| `task` | `Task` | FK NOT NULL | ManyToOne |
| `wasCorrect` | `boolean` | NOT NULL | |
| `pointsEarned` | `int` | NOT NULL, default 0 | |
| `answeredAt` | `Instant` | NOT NULL | |

Relations: many-to-many → `TaskOption` (the options the student selected)  
Join table: `attempt_answer_selected_options (answer_id, option_id)`

---

### 2.16 `UserPoints`

One row per user — updated after every completed attempt.

```
Table: user_points
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `user` | `User` | FK NOT NULL, unique | OneToOne |
| `totalPoints` | `int` | NOT NULL, default 0 | |
| `currentLevel` | `int` | NOT NULL, default 1 | derived from totalPoints |
| `currentStreak` | `int` | NOT NULL, default 0 | consecutive days active |
| `longestStreak` | `int` | NOT NULL, default 0 | |
| `lastActivityAt` | `Instant` | nullable | |

---

### 2.17 `AuditLog`

```
Table: audit_logs
```

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| `id` | `Long` | PK, auto-generated | |
| `actor` | `User` | FK NOT NULL | ManyToOne (who did it) |
| `action` | `AuditAction` (enum) | NOT NULL | see below |
| `targetType` | `String` | NOT NULL | entity class name |
| `targetId` | `Long` | nullable | PK of affected entity |
| `details` | `String` | nullable | free-text context |
| `timestamp` | `Instant` | NOT NULL | |

---

### 2.18 `AuditAction` (enum)

Values: `USER_CREATED`, `USER_UPDATED`, `USER_DELETED`, `PASSWORD_RESET`, `SCENARIO_PUBLISHED`, `SCENARIO_ARCHIVED`, `CLASS_CREATED`, `CLASS_UPDATED`, `CLASS_DELETED`, `LICENSE_ACTIVATED`

---

## 3. Repositories

All extend `JpaRepository<Entity, Long>`. Custom queries listed per interface.

---

### 3.1 `SchoolRepository`

```java
interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);
    boolean existsByName(String name);
}
```

---

### 3.2 `SchoolClassRepository`

```java
interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllBySchoolId(Long schoolId);
    List<SchoolClass> findAllByTeacherId(Long teacherId);
    boolean existsByNameAndSchoolId(String name, Long schoolId);
}
```

---

### 3.3 `UserRepository`

```java
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findAllBySchoolClassId(Long classId);
    List<User> findAllBySchoolId(Long schoolId);
    List<User> findAllBySchoolIdAndRole(Long schoolId, Role role);
}
```

---

### 3.4 `CategoryRepository`

```java
interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
```

---

### 3.5 `ScenarioRepository`

```java
interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findAllByCategoryId(Long categoryId);
    List<Scenario> findAllByStatus(ScenarioStatus status);
    List<Scenario> findAllByCategoryIdAndStatus(Long categoryId, ScenarioStatus status);
    List<Scenario> findAllByCreatedById(Long userId);
}
```

---

### 3.6 `TaskRepository`

```java
interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByScenarioIdOrderByOrderIndex(Long scenarioId);
}
```

---

### 3.7 `TaskOptionRepository`

```java
interface TaskOptionRepository extends JpaRepository<TaskOption, Long> {
    List<TaskOption> findAllByTaskId(Long taskId);
    void deleteAllByTaskId(Long taskId);
}
```

---

### 3.8 `StudentProgressRepository`

```java
interface StudentProgressRepository extends JpaRepository<StudentProgress, Long> {
    Optional<StudentProgress> findByStudentIdAndScenarioId(Long studentId, Long scenarioId);
    List<StudentProgress> findAllByStudentId(Long studentId);
    List<StudentProgress> findAllByScenarioId(Long scenarioId);
    // For class leaderboard: get all progress records for students in a class
    @Query("SELECT sp FROM StudentProgress sp WHERE sp.student.schoolClass.id = :classId")
    List<StudentProgress> findAllByStudentClassId(@Param("classId") Long classId);
}
```

---

### 3.9 `AttemptResultRepository`

```java
interface AttemptResultRepository extends JpaRepository<AttemptResult, Long> {
    List<AttemptResult> findAllByStudentId(Long studentId);
    List<AttemptResult> findAllByStudentIdAndScenarioId(Long studentId, Long scenarioId);
    List<AttemptResult> findAllByScenarioId(Long scenarioId);
    Optional<AttemptResult> findByIdAndStudentId(Long id, Long studentId);
    // Fetch incomplete attempt
    Optional<AttemptResult> findByStudentIdAndScenarioIdAndCompletedAtIsNull(Long studentId, Long scenarioId);
}
```

---

### 3.10 `UserPointsRepository`

```java
interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    Optional<UserPoints> findByUserId(Long userId);
    // Global leaderboard (top N)
    List<UserPoints> findTop50ByOrderByTotalPointsDesc();
    // School leaderboard
    @Query("SELECT up FROM UserPoints up WHERE up.user.school.id = :schoolId ORDER BY up.totalPoints DESC")
    List<UserPoints> findTop50BySchoolIdOrderByTotalPointsDesc(@Param("schoolId") Long schoolId);
    // Class leaderboard
    @Query("SELECT up FROM UserPoints up WHERE up.user.schoolClass.id = :classId ORDER BY up.totalPoints DESC")
    List<UserPoints> findAllByClassIdOrderByTotalPointsDesc(@Param("classId") Long classId);
}
```

---

### 3.11 `AuditLogRepository`

```java
interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByActorIdOrderByTimestampDesc(Long actorId);
    List<AuditLog> findAllByTargetTypeAndTargetId(String targetType, Long targetId);
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
}
```

---

## 4. Service Layer

### 4.1 `AuthService`

```java
interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshTokenRequest request);
    void logout(String token);                         // invalidate if using token store
}
```

**Implementation notes:**
- Authenticate with `UsernamePasswordAuthenticationToken`
- Issue JWT signed with HMAC-SHA256; include `sub` (userId), `role`, `schoolId`, `exp`
- Refresh token: separate longer-lived JWT stored in DB or Redis; swap for new access token
- Update `user.lastLoginAt` on successful login
- Record no audit log (too noisy); failed login attempts can be logged at WARN level

---

### 4.2 `UserService`

```java
interface UserService {
    UserResponse createUser(CreateUserRequest request, UserPrincipal actor);
    UserResponse getUserById(Long id, UserPrincipal actor);
    List<UserResponse> getAllUsers(UserPrincipal actor);           // scoped to actor's school
    UserResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal actor);
    void deleteUser(Long id, UserPrincipal actor);
    void resetPassword(Long id, ResetPasswordRequest request, UserPrincipal actor);
}
```

**Implementation notes:**
- `createUser`: TEACHER may only create STUDENTs in their own class; ADMIN may create any role
- Password always bcrypt-hashed before persistence; never returned in responses
- `deleteUser`: soft-delete (`active = false`), not a physical DELETE
- `resetPassword`: only TEACHER (for own class students) and ADMIN
- Audit log on: create, delete, password reset

---

### 4.3 `SchoolService`

```java
interface SchoolService {
    SchoolResponse createSchool(CreateSchoolRequest request);   // ADMIN only
    SchoolResponse getSchoolById(Long id);
    List<SchoolResponse> getAllSchools();
    SchoolResponse updateSchool(Long id, UpdateSchoolRequest request);
    SchoolResponse activateLicense(Long id, String licenseCode, LocalDate expiry);
}
```

---

### 4.4 `SchoolClassService`

```java
interface SchoolClassService {
    ClassResponse createClass(CreateClassRequest request, UserPrincipal actor);
    ClassResponse getClassById(Long id, UserPrincipal actor);
    List<ClassResponse> getClassesForSchool(Long schoolId, UserPrincipal actor);
    ClassResponse updateClass(Long id, UpdateClassRequest request, UserPrincipal actor);
    void deleteClass(Long id, UserPrincipal actor);
    List<UserResponse> getStudentsInClass(Long classId, UserPrincipal actor);
}
```

---

### 4.5 `CategoryService`

```java
interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);         // only if no scenarios reference it
}
```

---

### 4.6 `ScenarioService`

```java
interface ScenarioService {
    ScenarioResponse createScenario(CreateScenarioRequest request, UserPrincipal actor);
    ScenarioResponse getScenarioById(Long id, UserPrincipal actor);
    List<ScenarioResponse> getScenarios(Long categoryId, ScenarioStatus status, UserPrincipal actor);
    ScenarioResponse updateScenario(Long id, UpdateScenarioRequest request, UserPrincipal actor);
    void deleteScenario(Long id, UserPrincipal actor);
    ScenarioResponse publishScenario(Long id, UserPrincipal actor);
    ScenarioResponse archiveScenario(Long id, UserPrincipal actor);
}
```

**Implementation notes:**
- Students see only `PUBLISHED` scenarios
- Teachers see own scenarios in any status; can only edit own or school-scoped DRAFT scenarios
- `publishScenario`: status `DRAFT → PUBLISHED`, increments `version`, audits `SCENARIO_PUBLISHED`
- `deleteScenario`: only allowed while `DRAFT`; published scenarios must be archived instead

---

### 4.7 `TaskService`

```java
interface TaskService {
    TaskResponse addTask(Long scenarioId, CreateTaskRequest request, UserPrincipal actor);
    TaskResponse updateTask(Long scenarioId, Long taskId, UpdateTaskRequest request, UserPrincipal actor);
    void deleteTask(Long scenarioId, Long taskId, UserPrincipal actor);
    List<TaskResponse> getTasksForScenario(Long scenarioId, UserPrincipal actor);
    void reorderTasks(Long scenarioId, ReorderTasksRequest request, UserPrincipal actor);
}
```

**Implementation notes:**
- Only allowed on `DRAFT` scenarios; published scenarios are locked
- `reorderTasks` accepts ordered list of taskIds; reassigns `orderIndex` in a transaction
- STUDENT receives tasks without `isCorrect` flags on options (masked in response DTO)

---

### 4.8 `AttemptService`

```java
interface AttemptService {
    AttemptResponse startAttempt(StartAttemptRequest request, UserPrincipal actor);
    AttemptResponse submitAnswer(Long attemptId, SubmitAnswersRequest request, UserPrincipal actor);
    AttemptResponse completeAttempt(Long attemptId, UserPrincipal actor);
    AttemptResponse getAttempt(Long attemptId, UserPrincipal actor);
}
```

**Implementation notes:**
- Only one incomplete attempt per (student, scenario) allowed; starting a new one while one is open throws `409 Conflict`
- `submitAnswer`: validates selected options belong to the task; evaluates correctness server-side; never trust client scoring
- `completeAttempt`: calculates final score, updates `StudentProgress` (best score, attempts count), triggers `PointsService.award()`
- Returns feedback texts per task in the completed attempt response

---

### 4.9 `ProgressService`

```java
interface ProgressService {
    List<ProgressResponse> getProgressForCurrentUser(UserPrincipal actor);
    List<ProgressResponse> getProgressForStudent(Long studentId, UserPrincipal actor);
    ScenarioProgressResponse getProgressForScenario(Long scenarioId, UserPrincipal actor);
}
```

---

### 4.10 `LeaderboardService`

```java
interface LeaderboardService {
    LeaderboardResponse getGlobalLeaderboard();
    LeaderboardResponse getSchoolLeaderboard(Long schoolId);
    LeaderboardResponse getClassLeaderboard(Long classId, UserPrincipal actor);
}
```

---

### 4.11 `StatsService`

```java
interface StatsService {
    ClassStatsResponse getClassStats(Long classId, UserPrincipal actor);
    ScenarioStatsResponse getScenarioStats(Long scenarioId, UserPrincipal actor);
    StudentStatsResponse getStudentStats(Long studentId, UserPrincipal actor);
}
```

**Implementation notes:**
- `ClassStatsResponse`: list of students with completion count, avg score, total points
- `ScenarioStatsResponse`: total attempts, avg score, completion rate, per-task accuracy breakdown
- `StudentStatsResponse`: scenarios completed, total points, streak, per-category completion rate

---

### 4.12 `PointsService`

```java
// Not exposed via REST — called internally by AttemptService
interface PointsService {
    void award(Long userId, int pointsEarned);   // update UserPoints, recalculate level/streak
}
```

**Level thresholds (configurable):**

| Level | Points required |
|-------|----------------|
| 1 | 0 |
| 2 | 100 |
| 3 | 300 |
| 4 | 600 |
| 5 | 1000 |
| ... | +500 per level |

---

### 4.13 `AuditService`

```java
// Not exposed via REST — called internally
interface AuditService {
    void log(UserPrincipal actor, AuditAction action, String targetType, Long targetId, String details);
}
```

---

## 5. REST API Endpoints

### Access control legend

| Symbol | Meaning |
|--------|---------|
| 🎓 | STUDENT |
| 👩‍🏫 | TEACHER |
| 🔑 | ADMIN |
| 🔓 | Unauthenticated (public) |

---

### 5.1 Auth — `AuthController`

#### `POST /api/v1/auth/login` 🔓

**Request body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response `200`:**
```json
{
  "accessToken": "string (JWT)",
  "refreshToken": "string (JWT)",
  "expiresIn": 3600,
  "userId": 1,
  "role": "STUDENT | TEACHER | ADMIN"
}
```

**Errors:** `401` invalid credentials · `403` account inactive

---

#### `POST /api/v1/auth/refresh` 🔓

**Request body:**
```json
{ "refreshToken": "string" }
```

**Response `200`:** same shape as login response

**Errors:** `401` invalid or expired refresh token

---

#### `POST /api/v1/auth/logout` 🎓👩‍🏫🔑

No body. Invalidates the current refresh token.

**Response `204`**

---

### 5.2 Users — `UserController`

#### `GET /api/v1/users` 👩‍🏫🔑

Returns users scoped to the caller's school. TEACHERs see students in their classes. ADMINs see all users in their school.

**Query params:** `role` (optional filter), `classId` (optional filter)

**Response `200`:**
```json
[
  {
    "id": 1,
    "username": "string",
    "role": "STUDENT",
    "schoolId": 1,
    "classId": 2,
    "active": true,
    "createdAt": "2026-01-01T00:00:00Z",
    "lastLoginAt": "2026-01-15T09:00:00Z"
  }
]
```

---

#### `POST /api/v1/users` 👩‍🏫🔑

Creates a new user. TEACHER can only create `STUDENT` in own class. ADMIN can create any role.

**Request body:**
```json
{
  "username": "string (3–30 chars, alphanumeric + underscore)",
  "password": "string (min 6 chars)",
  "role": "STUDENT | TEACHER | ADMIN",
  "classId": 2
}
```

**Response `201`:** `UserResponse`

**Errors:** `400` validation · `409` username already taken · `403` role escalation

---

#### `GET /api/v1/users/{id}` 🎓👩‍🏫🔑

STUDENT: only own profile. TEACHER: own profile + students in own class. ADMIN: any.

**Response `200`:** `UserResponse`

**Errors:** `403` access denied · `404` not found

---

#### `PUT /api/v1/users/{id}` 👩‍🏫🔑

**Request body:**
```json
{
  "username": "string (optional)",
  "classId": 3,
  "active": true
}
```

**Response `200`:** `UserResponse`

**Errors:** `400` · `403` · `404` · `409` username taken

---

#### `DELETE /api/v1/users/{id}` 👩‍🏫🔑

Soft-deletes (sets `active = false`). TEACHER can only deactivate students in own class.

**Response `204`**

---

#### `POST /api/v1/users/{id}/reset-password` 👩‍🏫🔑

**Request body:**
```json
{ "newPassword": "string (min 6 chars)" }
```

**Response `204`**

**Errors:** `403` · `404`

---

### 5.3 Schools — `SchoolController`

#### `GET /api/v1/schools` 🔑

**Response `200`:** array of `SchoolResponse`

---

#### `POST /api/v1/schools` 🔑

**Request body:**
```json
{ "name": "string" }
```

**Response `201`:** `SchoolResponse`

**Errors:** `400` · `409` name taken

---

#### `GET /api/v1/schools/{id}` 🔑

**Response `200`:** `SchoolResponse`

---

#### `PUT /api/v1/schools/{id}` 🔑

**Request body:**
```json
{ "name": "string (optional)" }
```

**Response `200`:** `SchoolResponse`

---

#### `POST /api/v1/schools/{id}/activate-license` 🔑

**Request body:**
```json
{
  "expiry": "2027-01-01"
}
```

**Response `200`:** `SchoolResponse` with updated `licenseStatus` and `licenseExpiry`

---

### 5.4 Classes — `SchoolClassController`

#### `GET /api/v1/classes` 👩‍🏫🔑

TEACHER sees own classes. ADMIN sees all classes in school.

**Query params:** `schoolId` (ADMIN only)

**Response `200`:** array of `ClassResponse`

---

#### `POST /api/v1/classes` 👩‍🏫🔑

**Request body:**
```json
{
  "name": "string",
  "schoolId": 1,
  "teacherId": 5
}
```

**Response `201`:** `ClassResponse`

**Errors:** `400` · `409` class name already exists in school

---

#### `GET /api/v1/classes/{id}` 👩‍🏫🔑

**Response `200`:** `ClassResponse`

---

#### `PUT /api/v1/classes/{id}` 👩‍🏫🔑

**Request body:**
```json
{
  "name": "string (optional)",
  "teacherId": 6
}
```

**Response `200`:** `ClassResponse`

---

#### `DELETE /api/v1/classes/{id}` 🔑

Only ADMIN. Fails if class still has students.

**Response `204`**

**Errors:** `409` class has students

---

#### `GET /api/v1/classes/{id}/students` 👩‍🏫🔑

**Response `200`:** array of `UserResponse`

---

### 5.5 Categories — `CategoryController`

#### `GET /api/v1/categories` 🎓👩‍🏫🔑

**Response `200`:**
```json
[
  { "id": 1, "name": "Chemie", "description": "string", "iconUrl": "string" }
]
```

---

#### `POST /api/v1/categories` 👩‍🏫🔑

**Request body:**
```json
{ "name": "string", "description": "string", "iconUrl": "string" }
```

**Response `201`:** `CategoryResponse`

**Errors:** `400` · `409`

---

#### `GET /api/v1/categories/{id}` 🎓👩‍🏫🔑

**Response `200`:** `CategoryResponse`

---

#### `PUT /api/v1/categories/{id}` 👩‍🏫🔑

**Request body:** same shape as create, all fields optional

**Response `200`:** `CategoryResponse`

---

#### `DELETE /api/v1/categories/{id}` 🔑

**Response `204`**

**Errors:** `409` category has associated scenarios

---

### 5.6 Scenarios — `ScenarioController`

#### `GET /api/v1/scenarios` 🎓👩‍🏫🔑

Students see `PUBLISHED` only. Teachers see all statuses in own school.

**Query params:** `categoryId` (optional), `status` (optional, Teacher/Admin only), `page`, `size`

**Response `200`:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "string",
      "description": "string",
      "difficulty": "EASY",
      "status": "PUBLISHED",
      "version": 1,
      "categoryId": 2,
      "categoryName": "Chemie",
      "taskCount": 5,
      "createdAt": "2026-01-01T00:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 42
}
```

---

#### `POST /api/v1/scenarios` 👩‍🏫🔑

**Request body:**
```json
{
  "title": "string",
  "description": "string",
  "difficulty": "EASY | MEDIUM | HARD",
  "categoryId": 1
}
```

**Response `201`:** `ScenarioResponse`

---

#### `GET /api/v1/scenarios/{id}` 🎓👩‍🏫🔑

**Response `200`:** `ScenarioResponse` including full `tasks` array with options  
Note: for STUDENT, `isCorrect` field is omitted from `TaskOption` in the response.

---

#### `PUT /api/v1/scenarios/{id}` 👩‍🏫🔑

Only `DRAFT` scenarios are editable.

**Request body:** all fields optional, same shape as create

**Response `200`:** `ScenarioResponse`

**Errors:** `400` · `403` not owner · `409` scenario is not in DRAFT status

---

#### `DELETE /api/v1/scenarios/{id}` 👩‍🏫🔑

Only `DRAFT` scenarios can be deleted.

**Response `204`**

**Errors:** `409` not in DRAFT status

---

#### `POST /api/v1/scenarios/{id}/publish` 👩‍🏫🔑

Transition: `DRAFT → PUBLISHED`. Requires at least one task with at least two options.

**Response `200`:** `ScenarioResponse` with `status: PUBLISHED`, incremented `version`

**Errors:** `400` scenario has no tasks · `409` already published or archived

---

#### `POST /api/v1/scenarios/{id}/archive` 👩‍🏫🔑

Transition: `PUBLISHED → ARCHIVED`.

**Response `200`:** `ScenarioResponse` with `status: ARCHIVED`

---

### 5.7 Tasks — `TaskController`

Base path: `/api/v1/scenarios/{scenarioId}/tasks`

#### `GET /api/v1/scenarios/{scenarioId}/tasks` 🎓👩‍🏫🔑

For STUDENTs: `isCorrect` stripped from options in response.

**Response `200`:** ordered array of `TaskResponse`

---

#### `POST /api/v1/scenarios/{scenarioId}/tasks` 👩‍🏫🔑

**Request body:**
```json
{
  "questionText": "string",
  "type": "SINGLE_CHOICE | MULTI_CHOICE | TRUE_FALSE",
  "points": 10,
  "feedbackCorrect": "string",
  "feedbackWrong": "string",
  "options": [
    { "text": "string", "isCorrect": true, "orderIndex": 0 },
    { "text": "string", "isCorrect": false, "orderIndex": 1 }
  ]
}
```

Validation: `SINGLE_CHOICE` / `TRUE_FALSE` must have exactly one correct option. `MULTI_CHOICE` must have at least one correct option.

**Response `201`:** `TaskResponse`

**Errors:** `400` · `409` scenario is not in DRAFT

---

#### `PUT /api/v1/scenarios/{scenarioId}/tasks/{taskId}` 👩‍🏫🔑

All fields optional, same shape as create. Replaces options entirely if `options` is provided.

**Response `200`:** `TaskResponse`

---

#### `DELETE /api/v1/scenarios/{scenarioId}/tasks/{taskId}` 👩‍🏫🔑

**Response `204`**

---

#### `PUT /api/v1/scenarios/{scenarioId}/tasks/reorder` 👩‍🏫🔑

**Request body:**
```json
{ "orderedTaskIds": [3, 1, 2, 4] }
```

**Response `200`:** updated ordered array of `TaskResponse`

---

### 5.8 Attempts — `AttemptController`

#### `POST /api/v1/attempts` 🎓

Start a new attempt.

**Request body:**
```json
{ "scenarioId": 1 }
```

**Response `201`:**
```json
{
  "id": 10,
  "scenarioId": 1,
  "startedAt": "2026-01-15T09:00:00Z",
  "completedAt": null,
  "scoreEarned": 0,
  "scoreMax": 50,
  "answers": []
}
```

**Errors:** `404` scenario not found · `409` already has an in-progress attempt for this scenario · `403` scenario not published

---

#### `GET /api/v1/attempts/{id}` 🎓

**Response `200`:** `AttemptResponse`

**Errors:** `403` not own attempt · `404`

---

#### `POST /api/v1/attempts/{id}/answers` 🎓

Submit answers for one or more tasks in the attempt.

**Request body:**
```json
{
  "answers": [
    {
      "taskId": 1,
      "selectedOptionIds": [3]
    }
  ]
}
```

**Response `200`:** `AttemptResponse` with updated `answers` array including `wasCorrect`, `pointsEarned`, `feedbackText`

**Errors:** `400` task not in scenario · `409` task already answered · `403` attempt is completed

---

#### `POST /api/v1/attempts/{id}/complete` 🎓

Finalise the attempt. Calculates total score, updates `StudentProgress`, awards points.

**Response `200`:** `AttemptResponse` with `completedAt` set and final `scoreEarned`

**Errors:** `409` already completed

---

### 5.9 Progress — `ProgressController`

#### `GET /api/v1/progress` 🎓

Returns the authenticated student's own progress across all scenarios.

**Response `200`:**
```json
[
  {
    "scenarioId": 1,
    "scenarioTitle": "string",
    "categoryName": "Chemie",
    "attempts": 3,
    "bestScore": 45,
    "scoreMax": 50,
    "completed": true,
    "lastAttemptAt": "2026-01-15T09:00:00Z"
  }
]
```

---

#### `GET /api/v1/progress/student/{studentId}` 👩‍🏫🔑

TEACHER: only for students in own class. ADMIN: any student.

**Response `200`:** same shape as above

---

#### `GET /api/v1/progress/scenario/{scenarioId}` 👩‍🏫🔑

All students' progress for a given scenario.

**Response `200`:**
```json
[
  {
    "studentId": 1,
    "username": "string",
    "attempts": 2,
    "bestScore": 40,
    "scoreMax": 50,
    "completed": false
  }
]
```

---

### 5.10 Leaderboard — `LeaderboardController`

#### `GET /api/v1/leaderboard` 🎓👩‍🏫🔑

Top 50 globally.

**Response `200`:**
```json
{
  "entries": [
    {
      "rank": 1,
      "userId": 5,
      "username": "string",
      "totalPoints": 1250,
      "level": 4
    }
  ]
}
```

---

#### `GET /api/v1/leaderboard/school/{schoolId}` 👩‍🏫🔑

Top 50 in the school.

**Response `200`:** same shape

---

#### `GET /api/v1/leaderboard/class/{classId}` 🎓👩‍🏫🔑

All students in the class, ranked.

**Response `200`:** same shape

---

### 5.11 Stats — `StatsController`

#### `GET /api/v1/stats/class/{classId}` 👩‍🏫🔑

TEACHER: own class only. ADMIN: any class in own school.

**Response `200`:**
```json
{
  "classId": 2,
  "className": "10a",
  "studentCount": 28,
  "students": [
    {
      "userId": 1,
      "username": "string",
      "totalPoints": 320,
      "scenariosCompleted": 4,
      "averageScore": 82.5
    }
  ]
}
```

---

#### `GET /api/v1/stats/scenario/{scenarioId}` 👩‍🏫🔑

**Response `200`:**
```json
{
  "scenarioId": 1,
  "title": "string",
  "totalAttempts": 120,
  "uniqueStudents": 45,
  "completionRate": 0.73,
  "averageScore": 38.2,
  "taskBreakdown": [
    {
      "taskId": 1,
      "questionText": "string (truncated)",
      "correctRate": 0.85
    }
  ]
}
```

---

#### `GET /api/v1/stats/student/{studentId}` 👩‍🏫🔑

TEACHER: only own class students. ADMIN: any.

**Response `200`:**
```json
{
  "userId": 1,
  "username": "string",
  "totalPoints": 450,
  "currentLevel": 3,
  "currentStreak": 5,
  "longestStreak": 12,
  "scenariosCompleted": 7,
  "totalAttempts": 22,
  "categoryBreakdown": [
    {
      "categoryName": "Chemie",
      "scenariosCompleted": 3,
      "averageScore": 78.0
    }
  ]
}
```

---

## 6. Security & Auth Configuration

### 6.1 `SecurityConfig`

- CSRF disabled (stateless REST API)
- `SessionCreationPolicy.STATELESS`
- `JwtAuthenticationFilter` registered before `UsernamePasswordAuthenticationFilter`
- Public endpoints: `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh`
- All other endpoints require authentication
- Method-level `@PreAuthorize` annotations used per endpoint for fine-grained RBAC

### 6.2 `JwtConfig`

Properties (via `application.yml`):

```yaml
jwt:
  secret: ${JWT_SECRET}           # env var, min 32 chars
  access-token-expiry: 3600       # seconds (1 hour)
  refresh-token-expiry: 604800    # seconds (7 days)
```

### 6.3 `JwtTokenProvider`

```java
class JwtTokenProvider {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Claims parseToken(String token);
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
```

### 6.4 `UserPrincipal`

Implements `UserDetails`. Fields: `id`, `username`, `password`, `role`, `schoolId`, `classId`, `authorities`.

---

## 7. DTOs

Key design rules:
- DTOs are plain Java records or Lombok `@Value` classes — no JPA annotations
- Request DTOs use Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Min`)
- Response DTOs never expose `passwordHash`
- `isCorrect` field on `TaskOption` is excluded from `TaskResponse` when caller is a STUDENT (handled in service mapping)

### 7.1 `LoginRequest`
`username: @NotBlank String`, `password: @NotBlank String`

### 7.2 `LoginResponse`
`accessToken`, `refreshToken`, `expiresIn: long`, `userId: Long`, `role: Role`

### 7.3 `CreateUserRequest`
`username: @NotBlank @Size(min=3, max=30) String`, `password: @NotBlank @Size(min=6) String`, `role: @NotNull Role`, `classId: Long (nullable)`

### 7.4 `UpdateUserRequest`
`username: @Size(min=3, max=30) String (nullable)`, `classId: Long (nullable)`, `active: Boolean (nullable)`

### 7.5 `ResetPasswordRequest`
`newPassword: @NotBlank @Size(min=6) String`

### 7.6 `UserResponse`
`id`, `username`, `role`, `schoolId`, `classId`, `active`, `createdAt`, `lastLoginAt`

### 7.7 `CreateSchoolRequest`
`name: @NotBlank @Size(max=100) String`

### 7.8 `SchoolResponse`
`id`, `name`, `licenseStatus`, `licenseExpiry`, `createdAt`

### 7.9 `CreateClassRequest`
`name: @NotBlank String`, `schoolId: @NotNull Long`, `teacherId: Long (nullable)`

### 7.10 `ClassResponse`
`id`, `name`, `schoolId`, `schoolName`, `teacherId`, `teacherUsername`, `studentCount`, `createdAt`

### 7.11 `CreateCategoryRequest`
`name: @NotBlank @Size(max=50) String`, `description: String (nullable)`, `iconUrl: String (nullable)`

### 7.12 `CategoryResponse`
`id`, `name`, `description`, `iconUrl`

### 7.13 `CreateScenarioRequest`
`title: @NotBlank @Size(max=100) String`, `description: String`, `difficulty: @NotNull ScenarioDifficulty`, `categoryId: @NotNull Long`

### 7.14 `ScenarioResponse`
`id`, `title`, `description`, `difficulty`, `status`, `version`, `categoryId`, `categoryName`, `taskCount`, `createdByUsername`, `createdAt`, `updatedAt`, `tasks: List<TaskResponse> (nullable, only when fetching single scenario)`

### 7.15 `CreateTaskRequest`
`questionText: @NotBlank String`, `type: @NotNull TaskType`, `points: @Min(1) int`, `feedbackCorrect: @NotBlank String`, `feedbackWrong: @NotBlank String`, `options: @NotEmpty List<TaskOptionRequest>`

### 7.16 `TaskOptionRequest`
`text: @NotBlank String`, `isCorrect: boolean`, `orderIndex: @Min(0) int`

### 7.17 `TaskResponse`
`id`, `scenarioId`, `orderIndex`, `questionText`, `type`, `points`, `feedbackCorrect`, `feedbackWrong`, `options: List<TaskOptionResponse>`

### 7.18 `TaskOptionResponse`
`id`, `text`, `orderIndex`, `isCorrect (omitted for STUDENT role)`

### 7.19 `StartAttemptRequest`
`scenarioId: @NotNull Long`

### 7.20 `SubmitAnswersRequest`
`answers: @NotEmpty List<AnswerItem>`

### 7.21 `AnswerItem`
`taskId: @NotNull Long`, `selectedOptionIds: @NotEmpty List<Long>`

### 7.22 `AttemptResponse`
`id`, `scenarioId`, `startedAt`, `completedAt`, `scoreEarned`, `scoreMax`, `answers: List<AttemptAnswerResponse>`

### 7.23 `AttemptAnswerResponse`
`taskId`, `selectedOptionIds`, `wasCorrect`, `pointsEarned`, `feedbackText`

---

## 8. Admin Shell (`AdminShell`)

Spring Shell commands for local/ops use. All commands require the application to be running.

| Command | Description |
|---------|-------------|
| `create-school --name "..."` | Create a new school |
| `activate-license --schoolId 1 --expiry 2027-01-01` | Activate school license |
| `create-admin --username "..." --password "..." --schoolId 1` | Bootstrap first admin user |
| `list-schools` | List all schools with license status |
| `list-users --schoolId 1` | List users for a school |
| `reset-password --userId 1 --password "..."` | Force-reset any user's password |
| `audit-log --limit 50` | Print last N audit entries |

---

## 9. Configuration Files

### 9.1 `application.yml` (development)

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/safetyspot
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate          # use Flyway/Liquibase in prod
    show-sql: false
    properties:
      hibernate.format_sql: true

jwt:
  secret: ${JWT_SECRET}
  access-token-expiry: 3600
  refresh-token-expiry: 604800

logging:
  level:
    spot.safety.ssbackend: DEBUG
```

### 9.2 `application-prod.yml` (Azure)

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://${AZURE_SQL_HOST};databaseName=safetyspot;encrypt=true
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.SQLServerDialect
```

---

## 10. Testing Strategy (TDD)

All services and controllers must have tests written **before or alongside** implementation.

### Test layers

| Layer | Tool | Location |
|-------|------|----------|
| Unit (service) | JUnit 5 + Mockito | `src/test/java/.../service/` |
| Integration (repository) | `@DataJpaTest` + H2 | `src/test/java/.../repository/` |
| Web (controller) | `@WebMvcTest` + MockMvc | `src/test/java/.../controller/` |
| Full integration | `@SpringBootTest` | `src/test/java/.../integration/` |

### Test classes to create

| Test class | Covers |
|-----------|--------|
| `AuthServiceTest` | login success, wrong password, inactive user, token refresh |
| `UserServiceTest` | create/update/delete, role escalation guard, password hashing |
| `ScenarioServiceTest` | lifecycle transitions (draft→published→archived), access rules |
| `TaskServiceTest` | add/remove tasks, reorder, lock on published scenario |
| `AttemptServiceTest` | start, submit answers (server-side scoring), complete, duplicate guard |
| `PointsServiceTest` | level calculation, streak logic |
| `LeaderboardServiceTest` | ordering, scope (global/school/class) |
| `AuthControllerTest` | login 200, login 401, refresh 401 |
| `ScenarioControllerTest` | student sees only PUBLISHED, teacher sees DRAFT |
| `AttemptControllerTest` | full play-through, 409 on duplicate attempt |
| `UserRepositoryTest` | findByUsername, existsByUsername |
| `StudentProgressRepositoryTest` | upsert logic, class-scoped query |

---

## 11. Error Handling

### Global `@RestControllerAdvice`

| Exception | HTTP status | Error code |
|-----------|-------------|------------|
| `EntityNotFoundException` | `404 Not Found` | `NOT_FOUND` |
| `AccessDeniedException` | `403 Forbidden` | `FORBIDDEN` |
| `UsernameAlreadyTakenException` | `409 Conflict` | `USERNAME_TAKEN` |
| `ScenarioNotEditableException` | `409 Conflict` | `SCENARIO_LOCKED` |
| `DuplicateAttemptException` | `409 Conflict` | `ATTEMPT_IN_PROGRESS` |
| `MethodArgumentNotValidException` | `400 Bad Request` | `VALIDATION_ERROR` |
| `BadCredentialsException` | `401 Unauthorized` | `INVALID_CREDENTIALS` |

**Error response shape:**
```json
{
  "status": 409,
  "error": "SCENARIO_LOCKED",
  "message": "Scenario is published and cannot be edited.",
  "timestamp": "2026-01-15T09:00:00Z"
}
```

---

## 12. Backlog Item Groups (Epics)

Each section below maps directly to a sprint/backlog epic.

### Epic 1 — Project Foundation
- Set up package structure
- Add MSSQL driver dependency to `pom.xml` (Azure prod)
- Configure `application.yml` and `application-prod.yml`
- Implement `GlobalExceptionHandler`
- Set up `SecurityConfig` (public/protected routes, stateless)
- Write base `SsbackendApplicationTests` health check test

### Epic 2 — Auth
- Implement `JwtConfig`, `JwtTokenProvider`, `JwtAuthenticationFilter`
- Implement `UserPrincipal` and `UserDetailsService`
- Implement `AuthService` + `AuthController`
- Write `AuthServiceTest` and `AuthControllerTest`

### Epic 3 — Org & Users
- Implement `School`, `SchoolClass`, `User` entities
- Implement `SchoolRepository`, `SchoolClassRepository`, `UserRepository`
- Implement `SchoolService` + `SchoolController`
- Implement `SchoolClassService` + `SchoolClassController`
- Implement `UserService` + `UserController`
- Implement `AuditService` + `AuditLog` entity
- Write all service + controller + repository tests for this epic

### Epic 4 — Content (Categories & Scenarios)
- Implement `Category`, `Scenario`, `Task`, `TaskOption` entities
- Implement all repositories
- Implement `CategoryService` + `CategoryController`
- Implement `ScenarioService` + `ScenarioController` (incl. publish/archive lifecycle)
- Implement `TaskService` + `TaskController` (incl. reorder)
- Write all tests

### Epic 5 — Gameplay & Scoring
- Implement `StudentProgress`, `AttemptResult`, `AttemptAnswer`, `UserPoints` entities
- Implement all repositories
- Implement `PointsService` (level/streak logic)
- Implement `AttemptService` + `AttemptController` (server-side scoring)
- Implement `ProgressService` + `ProgressController`
- Write all tests

### Epic 6 — Gamification & Stats
- Implement `LeaderboardService` + `LeaderboardController`
- Implement `StatsService` + `StatsController`
- Write all tests

### Epic 7 — Admin Shell
- Implement all `AdminShell` commands
- Write shell integration tests

### Epic 8 — Hardening & Azure Deployment
- Add MSSQL dialect and driver, test with Azure SQL profile
- Set up Azure App Service + Azure SQL via Azure Portal / Bicep
- Configure environment variables (`JWT_SECRET`, `DB_USER`, `DB_PASSWORD`)
- Smoke-test all endpoints against production

---
---

# SafetySpot – Frontend (Android) Implementation Plan

> **Scope:** Android mobile app (`ssmobile/`) — the thin client consuming the `ssbackend` REST API.
> **Stack:** Kotlin · Jetpack Compose (Material 3) · Compose BOM `2026.02.01` · `minSdk 24` / `targetSdk 36` · Java 21 toolchain
> **App module:** `:app` · **Namespace / appId:** `spot.safety.ssmobile`
> **Shared module:** `sscommon` (pure-Kotlin JVM, included via `includeBuild("../sscommon")`) — home for API contract DTOs that are framework-agnostic.
> **Architecture:** MVVM + unidirectional data flow (UDF), single-Activity Compose app, `Repository` pattern over a Retrofit API + Room cache.
> **Chosen libraries:** Retrofit + Moshi (networking/JSON) · Hilt (DI) · Room (offline cache) · DataStore Preferences (token storage) · Kotlin Coroutines + Flow (async) · Coil (image loading) · Navigation-Compose (routing).
> **Backend contract:** All endpoints under `/api/v1`, JWT Bearer auth. See **§5 REST API Endpoints** above — the frontend models and repositories mirror those DTOs exactly.

The mockups in `docs/v1/*.md` drive the screen inventory:
`SafetySpot_Anmelden` (auth/onboarding), `SafetySpot_home` (Start dashboard), `SafetySpot_szenarienanzeigen` (Szenarien browse), `SafetySpot_szenario` (gameplay), `SafetySpot_schulranking` (Ranking), `SafetySpot_profil` (Profil). Icon: `SafetySpotIcon`.

---

## F1. Build Configuration & Dependencies

Extend `gradle/libs.versions.toml` and `app/build.gradle.kts` (do **not** hand-roll versions — add to the version catalog).

**New version-catalog entries (`[versions]` / `[libraries]` / `[plugins]`):**

| Concern | Library | Catalog alias |
|---------|---------|---------------|
| DI | `com.google.dagger:hilt-android` + compiler | `hilt.android`, `hilt.compiler` |
| DI for Compose | `androidx.hilt:hilt-navigation-compose` | `androidx.hilt.navigation.compose` |
| Navigation | `androidx.navigation:navigation-compose` | `androidx.navigation.compose` |
| Networking | `com.squareup.retrofit2:retrofit` + `converter-moshi` | `retrofit`, `retrofit.moshi` |
| JSON | `com.squareup.moshi:moshi-kotlin` + codegen | `moshi.kotlin`, `moshi.codegen` |
| HTTP logging | `com.squareup.okhttp3:logging-interceptor` | `okhttp.logging` |
| Offline cache | `androidx.room:room-runtime` + `room-ktx` + compiler | `room.runtime`, `room.ktx`, `room.compiler` |
| Token storage | `androidx.datastore:datastore-preferences` | `androidx.datastore.preferences` |
| Images | `io.coil-kt.coil3:coil-compose` + `coil-network-okhttp` | `coil.compose`, `coil.network` |
| ViewModel-Compose | `androidx.lifecycle:lifecycle-viewmodel-compose` | `androidx.lifecycle.viewmodel.compose` |
| Coroutines test | `org.jetbrains.kotlinx:kotlinx-coroutines-test` | `kotlinx.coroutines.test` |
| MockWebServer | `com.squareup.okhttp3:mockwebserver` | `okhttp.mockwebserver` |
| Turbine (Flow test) | `app.cash.turbine:turbine` | `turbine` |

**Plugins to add:** `com.google.dagger.hilt.android`, `org.jetbrains.kotlin.plugin.serialization` (only if we later swap to kotlinx; Moshi codegen uses `com.google.devtools.ksp`), `androidx.room`, and **KSP** (`com.google.devtools.ksp`) for Moshi/Room/Hilt annotation processing.

**Manifest changes (`app/src/main/AndroidManifest.xml`):**
- Add `<uses-permission android:name="android.permission.INTERNET"/>`.
- Set a custom `android:name=".SafetySpotApp"` `Application` class annotated `@HiltAndroidApp`.
- Allow cleartext only for the local dev base URL via a debug-only network-security-config (prod uses HTTPS to Azure).

**Build config fields:** expose `BUILD_CONFIG` `API_BASE_URL` per build type (`debug` → `http://10.0.2.2:8080/api/v1/` for the emulator hitting localhost; `release` → Azure App Service HTTPS URL).

---

## F2. Module & Package Structure

```
ssmobile/app/src/main/java/spot/safety/ssmobile
├── SafetySpotApp.kt                 (@HiltAndroidApp)
├── MainActivity.kt                  (single activity, hosts NavHost)
├── di/
│   ├── NetworkModule.kt             (Retrofit, OkHttp, Moshi, interceptors)
│   ├── DatabaseModule.kt            (Room db + DAOs)
│   ├── DataStoreModule.kt           (token DataStore)
│   └── RepositoryModule.kt          (binds repo interfaces → impls)
├── data/
│   ├── remote/
│   │   ├── api/                     (Retrofit service interfaces, one per controller)
│   │   │   ├── AuthApi.kt
│   │   │   ├── UserApi.kt
│   │   │   ├── ScenarioApi.kt
│   │   │   ├── TaskApi.kt
│   │   │   ├── AttemptApi.kt
│   │   │   ├── ProgressApi.kt
│   │   │   ├── LeaderboardApi.kt
│   │   │   └── CategoryApi.kt
│   │   ├── dto/                     (request/response models mirroring §7 backend DTOs)
│   │   ├── interceptor/
│   │   │   ├── AuthInterceptor.kt   (adds Bearer token)
│   │   │   └── TokenAuthenticator.kt(401 → refresh → retry)
│   │   └── NetworkResult.kt         (sealed: Success/Error/Loading + ApiException mapping)
│   ├── local/
│   │   ├── SafetySpotDatabase.kt
│   │   ├── dao/                     (ScenarioDao, CategoryDao, PendingAttemptDao, ProfileDao)
│   │   └── entity/                  (Room @Entity cache rows)
│   ├── mapper/                      (DTO ↔ domain ↔ entity mappers)
│   └── repository/                  (impl classes)
│       ├── AuthRepositoryImpl.kt
│       ├── ScenarioRepositoryImpl.kt
│       ├── AttemptRepositoryImpl.kt
│       ├── ProgressRepositoryImpl.kt
│       ├── LeaderboardRepositoryImpl.kt
│       └── ProfileRepositoryImpl.kt
├── domain/
│   ├── model/                       (UI-facing domain models: User, Scenario, Task, Attempt, …)
│   └── repository/                  (repository interfaces)
├── ui/
│   ├── theme/                       (existing — to be re-skinned, see F3)
│   ├── components/                  (shared composables, see F3)
│   ├── navigation/
│   │   ├── SafetySpotNavHost.kt
│   │   ├── Destinations.kt          (typed routes)
│   │   └── BottomNavBar.kt
│   ├── auth/        (AuthScreen, AuthViewModel, AuthUiState)
│   ├── home/        (HomeScreen, HomeViewModel, HomeUiState)
│   ├── scenarios/   (ScenariosScreen, ScenariosViewModel, ScenariosUiState)
│   ├── scenario/    (ScenarioPlayScreen, ScenarioPlayViewModel, ScenarioPlayUiState)
│   ├── ranking/     (RankingScreen, RankingViewModel, RankingUiState)
│   └── profile/     (ProfileScreen, ProfileViewModel, ProfileUiState)
└── util/            (Result extensions, formatters e.g. "2.450", date utils)
```

**`sscommon`** hosts pure-Kotlin, serialization-agnostic API contract data classes (the DTO shapes from §7) so the request/response models are defined once. Android-specific annotations (Moshi `@Json`, Room) live in `:app`, mapping to/from the `sscommon` contract types. (`sscommon`'s placeholder `Main.kt` is removed and replaced by a `contract/` package.)

---

## F3. Design System (Theme & Reusable Components)

Re-skin the scaffolded `ui/theme` to match the mockups (currently the default purple Material starter).

**Color palette (`Color.kt`) — derived from mockups:**
- `BrandBlue` (dark blue title "Safety", headings) ≈ `#1B3A6B`
- `BrandGreen` (primary CTA, active nav, "Spot") ≈ `#34C759` / `#2FB344`
- `BrandCyan` (logo gradient top) ≈ `#4FC3F7`
- `PointsYellow` (star/points) ≈ `#FFC53D`
- Category accents: Chemie `BlueTint`, Werkraum `OrangeTint`, Sport `GreenTint`, Technik `PurpleTint`, Straßenverkehr `RedTint`
- Difficulty: `DifficultyEasy` (green), `DifficultyMedium` (amber), `DifficultyHard` (red)
- Answer buttons: `DangerPink` bg / red icon, `SafeGreen` bg / green check
- Background gradient: light pastel blue → faint mint green (used on `AuthScreen`)

**`Theme.kt`:** replace dynamic-color default with a fixed brand `lightColorScheme` (primary = `BrandGreen`, secondary = `BrandBlue`, tertiary = `PointsYellow`); keep an optional dark scheme. Disable `dynamicColor` by default so branding is consistent. Rename `SsmobileTheme` → keep name (used in MainActivity) but supply brand colors.

**`Type.kt`:** define typography scale — bold display for greetings/titles, medium for card labels, etc.

**Reusable components (`ui/components/`):**
- `PillButton` (solid green / outlined variants — Anmelden/Registrieren, filter tabs)
- `SafetySpotBottomBar` (4 tabs: Start, Szenarien, Ranking, Profil with active-green state)
- `MetricCard` (level badge card, points card)
- `StreakRow`, `ProgressBar` (XP / scenario completion)
- `CategoryTile` (colored icon block + scenario count)
- `ScenarioCard` (icon, title, subtitle, difficulty chip, task count, "Neu" tag)
- `DifficultyChip`, `TagChip`
- `PodiumColumn` (gold/silver/bronze), `LeaderboardRow` (with self-highlight)
- `SearchBar`, `FilterTabRow`
- `GradientBackground`, `SectionHeader` (title + "Alle anzeigen" link)
- `LoadingIndicator`, `ErrorState`, `EmptyState`

A `LocalNumberFormat` / `formatScore()` util renders scores with German thousands separator ("2.450").

---

## F4. Networking & Auth Token Handling

- **`NetworkModule`** builds an `OkHttpClient` with: `AuthInterceptor` (injects `Authorization: Bearer <accessToken>` from DataStore for protected calls), `HttpLoggingInterceptor` (debug only), and a `TokenAuthenticator` that, on `401`, calls `POST /api/v1/auth/refresh`, stores the new tokens, and retries once; on refresh failure it clears tokens and emits a global "logout" signal.
- **`TokenManager`** wraps DataStore: `accessToken`, `refreshToken`, `userId`, `role`, `expiresIn`; exposed as `Flow<AuthState>` consumed by the NavHost to decide start destination (logged-in vs. Anmelden) and by a guest mode flag.
- **`NetworkResult<T>`** sealed type; a `safeApiCall { }` helper maps HTTP errors to the backend error envelope (`{status,error,message,timestamp}` from §11) into typed `ApiException`s (`InvalidCredentials`, `UsernameTaken`, `ScenarioLocked`, `NotFound`, `Forbidden`, `Validation`, `Network`).
- **Guest mode** ("Als Gast ausprobieren"): no token; only public/cached content is shown; attempt submission is disabled with a prompt to register.

---

## F5. Domain Models & DTO Mapping

Mirror the backend DTOs (§7) as Moshi `@JsonClass(generateAdapter=true)` data classes in `data/remote/dto`, then map to clean `domain/model` types:

| Domain model | Backed by endpoints |
|--------------|--------------------|
| `AuthSession(accessToken, refreshToken, expiresIn, userId, role)` | `POST /auth/login`, `/auth/refresh` |
| `UserProfile(id, displayName, username, role, level, xp, xpForNextLevel, points, streakDays, badges)` | `GET /users/{id}`, `GET /progress`, `GET /stats/student/{id}` |
| `Category(id, name, color, iconKey, scenarioCount)` | `GET /categories` |
| `ScenarioSummary(id, title, subtitle, categoryKey, difficulty, taskCount, isNew, status, completed)` | `GET /scenarios` |
| `ScenarioDetail(... + tasks)` | `GET /scenarios/{id}`, `GET /scenarios/{id}/tasks` |
| `Task(id, type, prompt, contextText, imageUrl, options, points)` | task DTOs |
| `Attempt(id, scenarioId, scoreEarned, scoreMax, answers, completedAt)` | `POST /attempts`, `/answers`, `/complete` |
| `ProgressSummary(continueScenario, percentComplete, perScenario[])` | `GET /progress` |
| `LeaderboardEntry(rank, displayName, score, isCurrentUser, avatarKey)` + `scope` (CLASS/SCHOOL/WORLD) | `GET /leaderboard`, `/class/{id}`, `/school/{id}` |

> **Contract gap to flag for backend:** mockups reference fields not yet in §5 — `streakDays`, `badges` count, `level`/`rank` title ("Sicherheitsprofi"), category `color`/`iconKey`, scenario `subtitle` + `isNew`, leaderboard avatar, and a "continue/resume" pointer in `GET /progress`. These should be added to the backend response DTOs (or derived client-side where trivial). Tracked as **Epic F8**.

---

## F6. Screens (mockup → implementation)

Each screen = `@Composable XScreen(state, onEvent)` (stateless, previewable) + `XViewModel` (exposes `StateFlow<XUiState>`, handles events) + `XUiState` (Loading/Content/Error). All wired through Hilt + `hiltViewModel()`.

### F6.1 `AuthScreen` — `SafetySpot_Anmelden`
Gradient background, centered `SafetySpotIcon`, "Safety**Spot**" title + slogan *"Lern. Sicher. Stark."*, three actions: **Anmelden** (→ login form / sheet), **Registrieren** (→ register form), **Als Gast ausprobieren** (sets guest mode → Home). Login form posts to `/auth/login`, stores tokens, navigates to Home. Validation + `InvalidCredentials` error surfacing.

### F6.2 `HomeScreen` — `SafetySpot_home`
Header greeting *"Hallo, {firstName}!"* + notification bell (alert dot). Two `MetricCard`s: Level badge ("12" / "Sicherheitsprofi") and Points ("2.450" + star). `StreakRow` ("7 Tage in Folge"). **Weitermachen** resume banner (category graphic, title, subtopic, 60% `ProgressBar`, **Fortsetzen** → ScenarioPlay). **Kategorien** grid (`CategoryTile` × N) + "Alle anzeigen" → Szenarien. Data from `GET /progress` + `GET /categories`. Bottom bar: Start active.

### F6.3 `ScenariosScreen` — `SafetySpot_szenarienanzeigen`
Title, `SearchBar` (+ filter icon), `FilterTabRow` (Alle / Neu / Beliebt / Abgeschlossen), vertical list of `ScenarioCard`s (icon, title, subtitle, "Neu" tag, `DifficultyChip`, task count). Backed by `GET /scenarios?status=PUBLISHED&search=&filter=`. Tapping a card → ScenarioPlay (or detail). Bottom bar: Szenarien active.

### F6.4 `ScenarioPlayScreen` — `SafetySpot_szenario`
Top header: back arrow, **"3 / 10"** step indicator/progress, reward tracker (gold star "120"). Category tag, mascot, bold question *"Ist das gefährlich?"* + instructions, illustration (`Coil`, `imageUrl`), context statement, two large decision buttons (**Gefährlich** pink/✗, **Nicht gefährlich** green/✓) — generalized to render any `TaskType` (boolean/single-choice/multi-choice). Flow: `POST /attempts` (start) → per-task `POST /attempts/{id}/answers` (shows `wasCorrect`/`feedbackText`/`pointsEarned`) → advance → `POST /attempts/{id}/complete` → results summary. Scoring is server-side; UI only reflects responses. Offline: queue answers as `PendingAttempt`.

### F6.5 `RankingScreen` — `SafetySpot_schulranking`
Scope tabs **Klasse / Schule / Weltweit** (pills). `Podium` for top 3 (gold/silver/bronze columns, avatars, scores) + scrollable `LeaderboardRow` list (ranks 4–8…), current user row highlighted light-green. Backed by `GET /leaderboard`, `/leaderboard/class/{id}`, `/leaderboard/school/{id}`. Bottom bar: Ranking active.

### F6.6 `ProfileScreen` — `SafetySpot_profil`
Header "Profil" + settings gear. Identity card: avatar, full name, "Level 12" + XP `ProgressBar` ("2.450 / 3.000 XP"), three stat columns (Punkte / Tage Streak / Abzeichen). Nav list blocks: Progress (*Mein Fortschritt*, *Abzeichen*, *Meine Szenarien*), Support (*Einstellungen*, *Hilfe & Feedback*), and **Abmelden** (red, clears tokens → Auth, `POST /auth/logout`). Backed by `GET /users/{id}` + `GET /stats/student/{id}`. Bottom bar: Profil active.

---

## F7. Navigation

- Single `NavHost` (`SafetySpotNavHost`) with typed destinations: `Auth`, `Home`, `Scenarios`, `ScenarioPlay(scenarioId)`, `Ranking`, `Profile` (+ later: `ScenarioResult`, `Settings`, `Badges`).
- `SafetySpotBottomBar` shown only on the four top-level tab destinations (Home/Scenarios/Ranking/Profile), hidden on Auth and ScenarioPlay.
- **Start destination** decided by `TokenManager.authState`: authenticated/guest → `Home`, else → `Auth`.
- Global logout (token cleared / refresh failed) pops back stack to `Auth`.

---

## F8. Offline Cache & Sync (Room)

Per the project spec ("app may cache scenarios locally and sync pending attempts when back online"):
- **Cache** categories, scenario summaries/details, and the last profile snapshot in Room; repositories follow a single-source-of-truth pattern (expose `Flow` from Room, refresh from network).
- **`PendingAttempt` / `PendingAnswer`** tables queue answers submitted while offline; a `SyncWorker` (WorkManager — optional add) flushes them to `/attempts/.../answers` + `/complete` when connectivity returns.
- Guest mode reads only cached/public content.

---

## F9. Testing (TDD)

Mirror the backend's TDD discipline.
- **Unit (`src/test`)**: ViewModels (via `kotlinx-coroutines-test` + Turbine for `StateFlow`), repositories (MockWebServer for Retrofit, in-memory Room), mappers, formatters (`formatScore`). Fake repositories for ViewModel tests.
- **Compose UI (`src/androidTest`)**: `createAndroidComposeRule` per screen — assert mockup elements render (e.g., Auth shows all three buttons; Home shows level/points/streak; bottom bar highlights active tab; ScenarioPlay shows 2 decision buttons and advances on answer). Replace the placeholder `ExampleInstrumentedTest`/`ExampleUnitTest`.
- **Contract tests**: Moshi adapters round-trip the §5 JSON example payloads.
- Commands: `./gradlew :app:testDebugUnitTest` (unit), `./gradlew :app:connectedDebugAndroidTest` (instrumented, needs emulator), `./gradlew :app:lintDebug`.

---

## F10. Frontend Backlog Item Groups (Epics)

### Epic F1 — Project Foundation & Build
- Add libraries to version catalog; apply Hilt/KSP/Room plugins in `app/build.gradle.kts`
- `SafetySpotApp` (`@HiltAndroidApp`), manifest INTERNET permission, BuildConfig `API_BASE_URL` per build type
- `NetworkModule`, `DatabaseModule`, `DataStoreModule`, `RepositoryModule`
- `NetworkResult` + `safeApiCall` + `ApiException` mapping; replace example tests with a base smoke test

### Epic F2 — Design System
- Re-skin `Color.kt` / `Theme.kt` / `Type.kt` to brand palette
- Build shared components (`PillButton`, `BottomBar`, `MetricCard`, `ScenarioCard`, `CategoryTile`, `ProgressBar`, `DifficultyChip`, `PodiumColumn`, `LeaderboardRow`, `SearchBar`, `FilterTabRow`, `GradientBackground`, state composables) with `@Preview`s + Compose UI tests

### Epic F3 — Auth & Navigation Shell
- DTOs + `AuthApi` + `AuthRepository`; `TokenManager` (DataStore); `AuthInterceptor` + `TokenAuthenticator`
- `AuthScreen` + `AuthViewModel` (login / register / guest)
- `SafetySpotNavHost`, `BottomNavBar`, start-destination logic, global logout
- Tests: AuthViewModel, token refresh flow, nav start destination

### Epic F4 — Home Dashboard
- `CategoryApi`/`ProgressApi`, repositories, mappers; `HomeViewModel` + `HomeScreen`
- Resume banner, metric cards, streak, category grid; tests

### Epic F5 — Scenarios Browse
- `ScenarioApi` + repository (with Room cache); `ScenariosViewModel` + `ScenariosScreen`
- Search + filter tabs; tests

### Epic F6 — Scenario Gameplay
- `AttemptApi`/`TaskApi` + `AttemptRepository`; `ScenarioPlayViewModel` + `ScenarioPlayScreen`
- Generalized task rendering, per-answer feedback, completion + result summary; tests

### Epic F7 — Ranking & Profile
- `LeaderboardApi`/`UserApi`/stats repositories; `RankingViewModel`+`RankingScreen`, `ProfileViewModel`+`ProfileScreen`
- Scope tabs, podium, self-highlight; profile identity card + logout; tests

### Epic F8 — Backend Contract Alignment
- Coordinate added response fields (streak, badges, level title, category color/icon, scenario subtitle/isNew, resume pointer, leaderboard avatar) — see §F5 gap note
- Update DTOs/mappers once backend ships them

### Epic F9 — Offline Cache & Sync
- Room entities/DAOs for scenarios, categories, profile; SSOT repositories
- `PendingAttempt` queue + sync on reconnect (optional `SyncWorker`)

### Epic F10 — Polish & Release
- Loading/error/empty states everywhere; accessibility (content descriptions for icons/mascot), large-text support
- App icon from `SafetySpotIcon`; release `API_BASE_URL` → Azure; R8/shrink config; device smoke test
