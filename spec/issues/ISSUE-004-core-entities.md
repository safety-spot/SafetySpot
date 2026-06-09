# ISSUE-004 — Core Entities: School, SchoolClass, User

**Epic:** Org & Users  
**Labels:** `backend`, `data-model`  
**Depends on:** ISSUE-001  
**Blocks:** ISSUE-003, ISSUE-005, ISSUE-006, ISSUE-007

---

## Summary

Implement the three foundational JPA entities (`School`, `SchoolClass`, `User`) and their
repositories. No controllers, no services — just the data layer. This can be worked on in
parallel with ISSUE-002 since there is no overlap in files.

---

## Acceptance Criteria

- [ ] `School`, `SchoolClass`, `User` are mapped as JPA entities with correct column names and constraints
- [ ] `Role` and `LicenseStatus` enums exist and are persisted as `STRING` (not ordinal)
- [ ] All three repositories extend `JpaRepository` and include the custom query methods listed below
- [ ] `@DataJpaTest` integration tests pass for each repository
- [ ] Passwords are **never** stored plain — `User` has no plain-text password field; only `passwordHash`
- [ ] `@PrePersist` / `@PreUpdate` hooks populate `createdAt` / `updatedAt` where applicable

---

## Technical Details

### Files to create

```
model/School.java
model/SchoolClass.java
model/User.java
model/Role.java                  (enum)
model/LicenseStatus.java         (enum)
repository/SchoolRepository.java
repository/SchoolClassRepository.java
repository/UserRepository.java
```

### `School`

```java
@Entity @Table(name = "schools")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class School {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus licenseStatus = LicenseStatus.INACTIVE;

    private LocalDate licenseExpiry;     // null = no active license

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
```

### `SchoolClass`

```java
@Entity @Table(name = "classes")
public class SchoolClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id")
    private School school;

    // Teacher is a User — set after User entity is created
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;              // nullable: class may exist before teacher is assigned

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
```

Note: `SchoolClass.teacher` is a `User` FK. This creates a circular dependency between
`SchoolClass` and `User`. Resolve with a `@ManyToOne(fetch = LAZY)` in one direction and
**no back-reference** in `User` (keep it uni-directional from `SchoolClass → User`).

### `User`

```java
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;       // bcrypt; NEVER plain text

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private SchoolClass schoolClass;   // null for TEACHER / ADMIN

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastLoginAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
```

### `Role` enum

```java
public enum Role { STUDENT, TEACHER, ADMIN }
```

### `LicenseStatus` enum

```java
public enum LicenseStatus { INACTIVE, ACTIVE, EXPIRED }
```

### Repositories

```java
interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByName(String name);
    boolean existsByName(String name);
}

interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllBySchoolId(Long schoolId);
    List<SchoolClass> findAllByTeacherId(Long teacherId);
    boolean existsByNameAndSchoolId(String name, Long schoolId);
}

interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findAllBySchoolClassId(Long classId);
    List<User> findAllBySchoolId(Long schoolId);
    List<User> findAllBySchoolIdAndRole(Long schoolId, Role role);
}
```

### Test classes

**`UserRepositoryTest`** (`@DataJpaTest`):
- `findByUsername_existingUser_returnsUser()`
- `findByUsername_missingUser_returnsEmpty()`
- `existsByUsername_taken_returnsTrue()`
- `findAllBySchoolClassId_returnsOnlyClassMembers()`

**`SchoolClassRepositoryTest`** (`@DataJpaTest`):
- `existsByNameAndSchoolId_duplicateName_returnsTrue()`
- `findAllBySchoolId_returnsOnlySchoolClasses()`

---

## Notes

- Do **not** add `@EqualsAndHashCode` or `@ToString` including lazy relations (Hibernate pitfall).
  Use `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` + `@EqualsAndHashCode.Include` on `id`.
- Use `@Column(insertable = false, updatable = false)` on `createdAt` if using `@PrePersist`.

---

## Out of Scope

- Service or controller layer (ISSUE-005, ISSUE-006)
- `AuditLog` entity (post-MVP)
- `UserPoints` entity (post-MVP)
