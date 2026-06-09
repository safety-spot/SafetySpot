# ISSUE-006 — School & Class Management API

**Epic:** Org & Users  
**Labels:** `backend`, `api`  
**Depends on:** ISSUE-002, ISSUE-004  
**Blocks:** —

---

## Summary

Expose REST endpoints for managing `School` and `SchoolClass` records. This is the
organisational backbone: admins create schools and classes, teachers are assigned to
classes, students are enrolled. Can be developed in parallel with ISSUE-005 since
the two controllers touch different files.

---

## Acceptance Criteria

- [ ] `POST /api/v1/schools` creates a school (ADMIN only); duplicate name returns `409`
- [ ] `GET /api/v1/schools` lists all schools (ADMIN only)
- [ ] `GET /api/v1/schools/{id}` returns a school (ADMIN only)
- [ ] `PUT /api/v1/schools/{id}` updates school name (ADMIN only)
- [ ] `POST /api/v1/schools/{id}/activate-license` sets `licenseStatus = ACTIVE` and stores `licenseExpiry` (ADMIN only)
- [ ] `POST /api/v1/classes` creates a class scoped to a school (ADMIN or TEACHER of that school)
- [ ] `GET /api/v1/classes` returns classes; TEACHERs see only own classes; ADMINs see all in school
- [ ] `GET /api/v1/classes/{id}` returns a class with student count
- [ ] `PUT /api/v1/classes/{id}` updates class name or assigned teacher (ADMIN/TEACHER)
- [ ] `DELETE /api/v1/classes/{id}` fails with `409` if the class still has students
- [ ] `GET /api/v1/classes/{id}/students` returns list of `UserResponse` for students in the class
- [ ] `SchoolControllerTest` and `SchoolClassControllerTest` cover all cases

---

## Technical Details

### Files to create

```
service/SchoolService.java
service/impl/SchoolServiceImpl.java
service/SchoolClassService.java
service/impl/SchoolClassServiceImpl.java
controller/SchoolController.java
controller/SchoolClassController.java
dto/school/CreateSchoolRequest.java
dto/school/UpdateSchoolRequest.java
dto/school/ActivateLicenseRequest.java
dto/school/SchoolResponse.java
dto/schoolclass/CreateClassRequest.java
dto/schoolclass/UpdateClassRequest.java
dto/schoolclass/ClassResponse.java
```

### School endpoints

| Method | Path | Role |
|--------|------|------|
| `GET` | `/api/v1/schools` | ADMIN |
| `POST` | `/api/v1/schools` | ADMIN |
| `GET` | `/api/v1/schools/{id}` | ADMIN |
| `PUT` | `/api/v1/schools/{id}` | ADMIN |
| `POST` | `/api/v1/schools/{id}/activate-license` | ADMIN |

### Class endpoints

| Method | Path | Role |
|--------|------|------|
| `GET` | `/api/v1/classes` | TEACHER, ADMIN |
| `POST` | `/api/v1/classes` | TEACHER, ADMIN |
| `GET` | `/api/v1/classes/{id}` | TEACHER, ADMIN |
| `PUT` | `/api/v1/classes/{id}` | TEACHER, ADMIN |
| `DELETE` | `/api/v1/classes/{id}` | ADMIN |
| `GET` | `/api/v1/classes/{id}/students` | TEACHER, ADMIN |

### DTOs

```java
record CreateSchoolRequest(
    @NotBlank @Size(max = 100) String name
) {}

record UpdateSchoolRequest(
    @Size(max = 100) String name     // optional
) {}

record ActivateLicenseRequest(
    @NotNull LocalDate expiry
) {}

record SchoolResponse(
    Long id,
    String name,
    LicenseStatus licenseStatus,
    LocalDate licenseExpiry,
    Instant createdAt
) {}

record CreateClassRequest(
    @NotBlank @Size(max = 50) String name,
    @NotNull Long schoolId,
    Long teacherId            // optional at creation time
) {}

record UpdateClassRequest(
    @Size(max = 50) String name,    // all optional
    Long teacherId
) {}

record ClassResponse(
    Long id,
    String name,
    Long schoolId,
    String schoolName,
    Long teacherId,
    String teacherUsername,
    int studentCount,
    Instant createdAt
) {}
```

### `SchoolService` interface

```java
public interface SchoolService {
    SchoolResponse createSchool(CreateSchoolRequest request);
    SchoolResponse getSchool(Long id);
    List<SchoolResponse> getAllSchools();
    SchoolResponse updateSchool(Long id, UpdateSchoolRequest request);
    SchoolResponse activateLicense(Long id, ActivateLicenseRequest request);
}
```

### `SchoolClassService` interface

```java
public interface SchoolClassService {
    ClassResponse createClass(CreateClassRequest request, UserPrincipal actor);
    ClassResponse getClass(Long id, UserPrincipal actor);
    List<ClassResponse> getClasses(UserPrincipal actor);
    ClassResponse updateClass(Long id, UpdateClassRequest request, UserPrincipal actor);
    void deleteClass(Long id, UserPrincipal actor);
    List<UserResponse> getStudents(Long classId, UserPrincipal actor);
}
```

### Key rules

- `deleteClass`: check `UserRepository.countBySchoolClassId(classId) > 0` — if true, throw `ClassNotEmptyException` → `409`
- `activateLicense`: also sets `licenseStatus = ACTIVE`; if `expiry` is in the past, reject with `400`
- School-scoping: all class operations must validate that the class belongs to the actor's school

### Add to `GlobalExceptionHandler` (ISSUE-001)

```java
// new exception
class ClassNotEmptyException extends RuntimeException { ... }
// handler: 409 CLASS_HAS_STUDENTS
```

### Test classes

**`SchoolServiceTest`** (Mockito):
- `createSchool_duplicateName_throwsConflict()`
- `activateLicense_pastDate_throwsBadRequest()`

**`SchoolClassServiceTest`** (Mockito):
- `deleteClass_withStudents_throwsConflict()`
- `getClasses_asTeacher_returnsOnlyOwnClasses()`

---

## Out of Scope

- License codes / voucher system (post-MVP)
- School self-registration (admin creates schools manually for MVP)
