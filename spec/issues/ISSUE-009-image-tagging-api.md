# ISSUE-009 — Image Tagging API (Student)

**Epic:** Gameplay (MVP)  
**Labels:** `backend`, `api`, `mvp`  
**Depends on:** ISSUE-002, ISSUE-007  
**Blocks:** ISSUE-010

---

## Summary

The core gameplay loop: a student submits a DANGEROUS or SAFE tag for an image, the backend
evaluates correctness server-side, stores the result, and returns immediate feedback.
Students cannot tag the same image twice.

---

## Acceptance Criteria

- [ ] `POST /api/v1/images/{id}/tag` stores an `ImageTag` and returns the result with correctness and feedback text
- [ ] `correctTag` is **never** sent from client — correctness is evaluated **server-side** by comparing `request.tag` to `image.correctTag`
- [ ] Tagging the same image twice returns `409`
- [ ] Tagging a non-existent or inactive image returns `404`
- [ ] Only authenticated users with role `STUDENT` can tag (TEACHER/ADMIN return `403`)
- [ ] `TagController` (or `ImageController` extended) and `TagService` are covered by `@WebMvcTest` + Mockito tests

---

## Technical Details

### Files to create

```
service/TagService.java
service/impl/TagServiceImpl.java
controller/TagController.java           (or add to ImageController — author's choice)
dto/tag/SubmitTagRequest.java
dto/tag/TagResponse.java
```

### Endpoint

#### `POST /api/v1/images/{imageId}/tag` — STUDENT only

Request:
```json
{ "tag": "DANGEROUS" }
```

Response `201`:
```json
{
  "imageId":     1,
  "tag":         "DANGEROUS",
  "correct":     true,
  "feedback":    "Richtig! Säuren ohne Schutzbrille sind gefährlich.",
  "taggedAt":    "2026-01-15T09:00:00Z"
}
```

Errors:
- `400` — `tag` field missing or invalid enum value
- `403` — caller is not STUDENT
- `404` — image not found or inactive
- `409 ALREADY_TAGGED` — student already tagged this image

### DTOs

```java
record SubmitTagRequest(
    @NotNull TagValue tag
) {}

record TagResponse(
    Long imageId,
    TagValue tag,
    boolean correct,
    String feedback,        // from Image: feedbackCorrect or feedbackWrong
    Instant taggedAt
) {}
```

### Feedback fields on `Image`

Add two new fields to the `Image` entity and `CreateImageRequest` / `UpdateImageRequest`
(coordinate with ISSUE-007 / ISSUE-008 implementer — add via a small amendment or include
directly if working in the same branch):

```java
// Image.java — additional fields
@Column(nullable = false)
private String feedbackCorrect;    // shown when student's tag matches correctTag

@Column(nullable = false)
private String feedbackWrong;      // shown when student's tag is wrong
```

Update `CreateImageRequest`:
```java
@NotBlank String feedbackCorrect,
@NotBlank String feedbackWrong
```

### `TagService` interface

```java
public interface TagService {
    TagResponse submitTag(Long imageId, SubmitTagRequest request, UserPrincipal actor);
}
```

### `TagServiceImpl` logic

```java
public TagResponse submitTag(Long imageId, SubmitTagRequest request, UserPrincipal actor) {
    // 1. Load image or throw EntityNotFoundException
    Image image = imageRepository.findById(imageId)
        .filter(Image::isActive)
        .orElseThrow(() -> new EntityNotFoundException("Image not found"));

    // 2. Check for duplicate tag
    if (imageTagRepository.existsByImageIdAndStudentId(imageId, actor.getId())) {
        throw new DuplicateTagException("Image already tagged");
    }

    // 3. Load student User entity
    User student = userRepository.findById(actor.getId()).orElseThrow();

    // 4. Evaluate correctness server-side
    boolean correct = request.tag() == image.getCorrectTag();
    String feedback = correct ? image.getFeedbackCorrect() : image.getFeedbackWrong();

    // 5. Persist
    ImageTag tag = ImageTag.builder()
        .image(image).student(student)
        .tag(request.tag()).correct(correct)
        .build();
    imageTagRepository.save(tag);

    return new TagResponse(imageId, request.tag(), correct, feedback, tag.getTaggedAt());
}
```

### Add to `GlobalExceptionHandler`

```java
class DuplicateTagException extends RuntimeException { ... }
// handler: 409 ALREADY_TAGGED
```

### Test class: `TagServiceTest` (Mockito)

| Test | Verifies |
|------|----------|
| `submitTag_correctAnswer_returnsCorrectTrue()` | server-side evaluation |
| `submitTag_wrongAnswer_returnsCorrectFalse()` | server-side evaluation |
| `submitTag_duplicate_throwsDuplicateTagException()` | 409 |
| `submitTag_inactiveImage_throwsEntityNotFound()` | 404 |
| `submitTag_neverExposeCorrectTag_inRequest()` | correctness from DB, not request |

### Test class: `TagControllerTest` (`@WebMvcTest`)

| Test | Verifies |
|------|----------|
| `submitTag_asTeacher_returns403()` | role guard |
| `submitTag_invalidTagValue_returns400()` | validation |
| `submitTag_duplicate_returns409()` | error shape |

---

## Out of Scope

- Re-tagging / changing a submitted tag
- Partial scoring based on confidence (post-MVP)
