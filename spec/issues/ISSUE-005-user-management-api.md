# ISSUE-005 — User Management API

**Epic:** Org & Users  
**Labels:** `backend`, `api`  
**Depends on:** ISSUE-002, ISSUE-004  
**Blocks:** —

---

## Summary

Implement the `UserService` and `UserController` so teachers can create, view, update,
and deactivate student accounts, and reset their passwords. Admins have the same powers
plus the ability to manage teachers. This is a self-contained vertical slice over the
`User` entity.

---

## Acceptance Criteria

- [ ] `GET /api/v1/users` returns users scoped to the caller's school; TEACHERs see only students in their own class(es); ADMINs see all users in their school
- [ ] `POST /api/v1/users` creates a user with a hashed password; TEACHER may only create `STUDENT` role; duplicate username returns `409`
- [ ] `GET /api/v1/users/{id}` — STUDENT sees own profile only; TEACHER sees own + class students; ADMIN sees any
- [ ] `PUT /api/v1/users/{id}` — TEACHER can update `username`, `classId`; ADMIN can additionally change `role` and `active`
- [ ] `DELETE /api/v1/users/{id}` soft-deletes (sets `active = false`); TEACHER limited to own class students
- [ ] `POST /api/v1/users/{id}/reset-password` — TEACHER (own class) or ADMIN; stores new bcrypt hash
- [ ] Trying to hard-delete or escalate roles returns `403`
- [ ] `UserControllerTest` covers all endpoints with `@WebMvcTest` + MockMvc; `UserServiceTest` uses Mockito

---

## Technical Details

### Files to create

```
service/UserService.java                 (interface)
service/impl/UserServiceImpl.java
controller/UserController.java
dto/user/CreateUserRequest.java
dto/user/UpdateUserRequest.java
dto/user/ResetPasswordRequest.java
dto/user/UserResponse.java
```

### Endpoint Summary

| Method | Path | Min. role | Notes |
|--------|------|-----------|-------|
| `GET` | `/api/v1/users` | TEACHER | query param `classId` (optional) |
| `POST` | `/api/v1/users` | TEACHER | |
| `GET` | `/api/v1/users/{id}` | STUDENT | STUDENT restricted to own id |
| `PUT` | `/api/v1/users/{id}` | TEACHER | |
| `DELETE` | `/api/v1/users/{id}` | TEACHER | soft-delete only |
| `POST` | `/api/v1/users/{id}/reset-password` | TEACHER | |

### DTOs

```java
record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Size(min = 6) String password,
    @NotNull Role role,
    Long classId           // required for STUDENT
) {}

record UpdateUserRequest(
    @Size(min = 3, max = 50) String username,   // all fields optional
    Long classId,
    Boolean active                               // ADMIN only
) {}

record ResetPasswordRequest(
    @NotBlank @Size(min = 6) String newPassword
) {}

record UserResponse(
    Long id,
    String username,
    Role role,
    Long schoolId,
    Long classId,
    boolean active,
    Instant createdAt,
    Instant lastLoginAt
) {}
```

### `UserService` interface

```java
public interface UserService {
    UserResponse createUser(CreateUserRequest request, UserPrincipal actor);
    UserResponse getUserById(Long id, UserPrincipal actor);
    List<UserResponse> getUsers(Long classId, UserPrincipal actor);
    UserResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal actor);
    void deactivateUser(Long id, UserPrincipal actor);
    void resetPassword(Long id, ResetPasswordRequest request, UserPrincipal actor);
}
```

### Key service rules

- Password is always encoded with `BCryptPasswordEncoder` before save.
- TEACHER can only manage users whose `schoolClass.teacher == actor.id`.
- TEACHER cannot set `role = TEACHER` or `role = ADMIN` — throws `AccessDeniedException`.
- Username uniqueness checked via `UserRepository.existsByUsername()` before save.

### Authorization with `@PreAuthorize`

```java
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
public ResponseEntity<UserResponse> createUser(...) { ... }

@PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
public ResponseEntity<UserResponse> getUser(@PathVariable Long id, ...) { ... }
```

Fine-grained ownership checks happen in the service, not the annotation.

### Test class: `UserServiceTest` (Mockito)

| Test | Verifies |
|------|----------|
| `createUser_asTeacher_hashesPassword()` | `passwordHash != rawPassword` |
| `createUser_duplicateUsername_throwsException()` | `UsernameAlreadyTakenException` |
| `createUser_teacherEscalatesRole_throwsAccessDenied()` | cannot create TEACHER/ADMIN |
| `deactivateUser_setsActiveFalse()` | physical row not deleted |
| `resetPassword_storesNewHash()` | new hash replaces old |
| `getUsers_asTeacher_onlyOwnClassReturned()` | scope check |

---

## Out of Scope

- Email / password recovery (no email in the system)
- Hard-delete of users
- `AuditLog` entries (post-MVP)
