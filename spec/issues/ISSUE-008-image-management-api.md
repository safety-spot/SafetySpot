# ISSUE-008 — Image Management API (Teacher)

**Epic:** Content (MVP)  
**Labels:** `backend`, `api`, `mvp`  
**Depends on:** ISSUE-002, ISSUE-007  
**Blocks:** ISSUE-009

---

## Summary

Teachers and admins can create, list, update, and deactivate images through a REST API.
Students can list and fetch published images to tag. Image storage uses a URL field — the
image binary is hosted externally (Azure Blob Storage URL, or any publicly accessible URL)
so we avoid file upload complexity in MVP.

---

## Acceptance Criteria

- [ ] `POST /api/v1/images` creates an image record (TEACHER/ADMIN); `correctTag` is stored but **never returned to students**
- [ ] `GET /api/v1/images` returns all active images; TEACHER/ADMIN get `correctTag` in the response; STUDENT does not
- [ ] `GET /api/v1/images/{id}` returns a single active image with the same role-based field masking
- [ ] `PUT /api/v1/images/{id}` updates metadata; only the uploader or ADMIN may edit
- [ ] `DELETE /api/v1/images/{id}` soft-deletes (sets `active = false`); only uploader or ADMIN
- [ ] `GET /api/v1/images/{id}/results` returns tagging results for that image (TEACHER/ADMIN only): per-student tag + correctness
- [ ] `ImageControllerTest` covers all cases; `ImageServiceTest` covers role-based masking and ownership

---

## Technical Details

### Files to create

```
service/ImageService.java
service/impl/ImageServiceImpl.java
controller/ImageController.java
dto/image/CreateImageRequest.java
dto/image/UpdateImageRequest.java
dto/image/ImageResponse.java           (with optional correctTag)
dto/image/ImageTagResultResponse.java
```

### Endpoints

| Method | Path | Min. role | Notes |
|--------|------|-----------|-------|
| `POST` | `/api/v1/images` | TEACHER | |
| `GET` | `/api/v1/images` | STUDENT | query: `category` (optional) |
| `GET` | `/api/v1/images/{id}` | STUDENT | |
| `PUT` | `/api/v1/images/{id}` | TEACHER | uploader or ADMIN only |
| `DELETE` | `/api/v1/images/{id}` | TEACHER | uploader or ADMIN only |
| `GET` | `/api/v1/images/{id}/results` | TEACHER | |

### DTOs

```java
record CreateImageRequest(
    @NotBlank @Size(max = 100) String title,
    @Size(max = 500) String description,
    @NotBlank String imageUrl,
    @Size(max = 50) String category,
    @NotNull TagValue correctTag
) {}

record UpdateImageRequest(
    @Size(max = 100) String title,         // all optional
    @Size(max = 500) String description,
    String imageUrl,
    @Size(max = 50) String category,
    TagValue correctTag
) {}

record ImageResponse(
    Long id,
    String title,
    String description,
    String imageUrl,
    String category,
    TagValue correctTag,    // null when serialized for STUDENT role
    Long uploadedById,
    String uploadedByUsername,
    Instant createdAt,
    Instant updatedAt
) {}

record ImageTagResultResponse(
    Long studentId,
    String username,
    TagValue tag,
    boolean correct,
    Instant taggedAt
) {}
```

### `ImageService` interface

```java
public interface ImageService {
    ImageResponse createImage(CreateImageRequest request, UserPrincipal actor);
    ImageResponse getImage(Long id, UserPrincipal actor);
    List<ImageResponse> getImages(String category, UserPrincipal actor);
    ImageResponse updateImage(Long id, UpdateImageRequest request, UserPrincipal actor);
    void deleteImage(Long id, UserPrincipal actor);
    List<ImageTagResultResponse> getImageResults(Long id, UserPrincipal actor);
}
```

### Role-based `correctTag` masking

The `ImageServiceImpl` must strip `correctTag` from responses when the caller has role `STUDENT`:

```java
private ImageResponse toResponse(Image image, UserPrincipal actor) {
    TagValue correctTag = actor.getRole() == Role.STUDENT ? null : image.getCorrectTag();
    return new ImageResponse(
        image.getId(), image.getTitle(), /* ... */,
        correctTag, /* ... */
    );
}
```

This logic must be covered by unit tests — it is a **security requirement** that students
cannot discover the correct answer from the API.

### Ownership guard

```java
private void assertCanEdit(Image image, UserPrincipal actor) {
    boolean isOwner = image.getUploadedBy().getId().equals(actor.getId());
    boolean isAdmin = actor.getRole() == Role.ADMIN;
    if (!isOwner && !isAdmin) throw new AccessDeniedException("Not image owner");
}
```

### Test class: `ImageServiceTest` (Mockito)

| Test | Verifies |
|------|----------|
| `getImage_asStudent_correctTagIsNull()` | masking |
| `getImage_asTeacher_correctTagPresent()` | no masking |
| `updateImage_notOwnerNotAdmin_throwsAccessDenied()` | ownership |
| `deleteImage_setsActiveFalse()` | soft-delete |
| `createImage_setsUploadedByFromActor()` | actor linkage |

---

## Out of Scope

- Binary file upload endpoint (images are referenced by URL only in MVP)
- Pagination on `GET /api/v1/images` (post-MVP; MVP returns all active images)
