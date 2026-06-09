# ISSUE-010 — Student Progress & Teacher Stats API

**Epic:** Progress & Stats (MVP)  
**Labels:** `backend`, `api`, `mvp`  
**Depends on:** ISSUE-009  
**Blocks:** —

---

## Summary

Two read-only endpoint groups: students see their own tagging history and score, teachers
see aggregated stats for their class. No write operations — all data is produced by ISSUE-009.

---

## Acceptance Criteria

- [ ] `GET /api/v1/progress` returns the authenticated student's tagging history (one entry per tagged image)
- [ ] `GET /api/v1/progress/summary` returns a single summary object: total tagged, correct count, accuracy percentage
- [ ] `GET /api/v1/stats/class/{classId}` returns per-student summaries for the class (TEACHER/ADMIN only)
- [ ] `GET /api/v1/stats/image/{imageId}` returns response distribution for one image (TEACHER/ADMIN only)
- [ ] TEACHER can only query stats for a class they teach; ADMIN can query any class in their school
- [ ] `ProgressControllerTest` and `StatsControllerTest` cover all cases

---

## Technical Details

### Files to create

```
service/ProgressService.java
service/impl/ProgressServiceImpl.java
service/StatsService.java
service/impl/StatsServiceImpl.java
controller/ProgressController.java
controller/StatsController.java
dto/progress/ProgressEntryResponse.java
dto/progress/ProgressSummaryResponse.java
dto/stats/ClassStatsResponse.java
dto/stats/StudentStatEntry.java
dto/stats/ImageStatsResponse.java
```

### Endpoints

| Method | Path | Role | Notes |
|--------|------|------|-------|
| `GET` | `/api/v1/progress` | STUDENT | own tagging history |
| `GET` | `/api/v1/progress/summary` | STUDENT | aggregate counts |
| `GET` | `/api/v1/stats/class/{classId}` | TEACHER, ADMIN | class overview |
| `GET` | `/api/v1/stats/image/{imageId}` | TEACHER, ADMIN | per-image results |

### DTOs

```java
// One row in the student's history
record ProgressEntryResponse(
    Long imageId,
    String imageTitle,
    String category,
    TagValue studentTag,
    boolean correct,
    Instant taggedAt
) {}

// Top-level summary for a student
record ProgressSummaryResponse(
    long totalTagged,
    long correctCount,
    double accuracyPercent    // correctCount / totalTagged * 100, or 0 if none tagged
) {}

// One row in the class stats table
record StudentStatEntry(
    Long userId,
    String username,
    long totalTagged,
    long correctCount,
    double accuracyPercent
) {}

// Class-level stats
record ClassStatsResponse(
    Long classId,
    String className,
    int totalImages,          // count of active images available to tag
    int studentCount,
    List<StudentStatEntry> students
) {}

// Per-image breakdown
record ImageStatsResponse(
    Long imageId,
    String title,
    TagValue correctTag,
    int totalResponses,
    int correctResponses,
    double correctRate,
    int dangerousCount,
    int safeCount
) {}
```

### `ProgressService` interface

```java
public interface ProgressService {
    List<ProgressEntryResponse> getHistory(UserPrincipal actor);
    ProgressSummaryResponse getSummary(UserPrincipal actor);
}
```

### `StatsService` interface

```java
public interface StatsService {
    ClassStatsResponse getClassStats(Long classId, UserPrincipal actor);
    ImageStatsResponse getImageStats(Long imageId, UserPrincipal actor);
}
```

### Implementation notes

**`ProgressServiceImpl.getHistory`:**
```java
return imageTagRepository.findAllByStudentId(actor.getId())
    .stream()
    .map(tag -> new ProgressEntryResponse(
        tag.getImage().getId(),
        tag.getImage().getTitle(),
        tag.getImage().getCategory(),
        tag.getTag(),
        tag.isCorrect(),
        tag.getTaggedAt()
    ))
    .sorted(Comparator.comparing(ProgressEntryResponse::taggedAt).reversed())
    .toList();
```

**`ProgressServiceImpl.getSummary`:**
```java
long total   = imageTagRepository.countByStudentId(actor.getId());
long correct = imageTagRepository.countByStudentIdAndCorrectTrue(actor.getId());
double accuracy = total == 0 ? 0.0 : (double) correct / total * 100;
return new ProgressSummaryResponse(total, correct, accuracy);
```

**`StatsServiceImpl.getClassStats`:**
1. Validate actor can access the class (TEACHER: own class; ADMIN: own school)
2. Load all `ImageTag` records for the class via `ImageTagRepository.findAllByStudentClassId(classId)`
3. Group by `student.id`, compute counts per student
4. Count `totalImages = imageRepository.countByActiveTrue()`

**`StatsServiceImpl.getImageStats`:**
1. Load all `ImageTag` for `imageId` via `ImageTagRepository.findAllByImageId(imageId)`
2. Count DANGEROUS / SAFE; correctRate = `correctResponses / totalResponses`

### Add to `ImageRepository`

```java
long countByActiveTrue();
```

### Test class: `ProgressServiceTest` (Mockito)

| Test | Verifies |
|------|----------|
| `getSummary_noTagsYet_returnsZeroAccuracy()` | divide by zero guard |
| `getSummary_allCorrect_returns100Percent()` | accuracy |
| `getHistory_returnsSortedDescending()` | sort order |

### Test class: `StatsServiceTest` (Mockito)

| Test | Verifies |
|------|----------|
| `getClassStats_asTeacher_wrongClass_throwsAccessDenied()` | scope |
| `getImageStats_computesCorrectRateFromTags()` | calculation |

---

## Out of Scope

- Global / school-wide leaderboard (post-MVP)
- Streak tracking (post-MVP)
- Historical time-series data
