# ISSUE-007 — Image & ImageTag Entities

**Epic:** Content (MVP)  
**Labels:** `backend`, `data-model`, `mvp`  
**Depends on:** ISSUE-004  
**Blocks:** ISSUE-008, ISSUE-009

---

## Summary

The MVP core: an `Image` record (uploaded by a teacher, has a known correct classification),
and an `ImageTag` record (a student's response — DANGEROUS or SAFE). This is purely the
data layer — no HTTP endpoints. Can be worked on in parallel with ISSUE-005 and ISSUE-006.

---

## Acceptance Criteria

- [ ] `Image` entity is mapped to `images` table with all fields below
- [ ] `ImageTag` entity is mapped to `image_tags` table; unique constraint on `(image_id, student_id)`
- [ ] `TagValue` enum exists with values `DANGEROUS` and `SAFE`, persisted as string
- [ ] `ImageRepository` and `ImageTagRepository` exist with custom queries listed below
- [ ] `@DataJpaTest` integration tests pass for both repositories

---

## Technical Details

### Files to create

```
model/Image.java
model/ImageTag.java
model/TagValue.java              (enum)
repository/ImageRepository.java
repository/ImageTagRepository.java
```

### `Image`

```java
@Entity @Table(name = "images")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Image {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String imageUrl;       // URL to the stored image (Azure Blob / static file server)

    @Column(length = 50)
    private String category;       // free-text category label e.g. "Chemie", "Werkraum"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagValue correctTag;   // the ground-truth answer: DANGEROUS or SAFE

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;       // the TEACHER or ADMIN who added this image

    @Column(nullable = false)
    private boolean active = true; // soft-delete

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }
}
```

### `ImageTag`

Records a single student's answer for a single image. One row per (student, image) pair.

```java
@Entity @Table(
    name = "image_tags",
    uniqueConstraints = @UniqueConstraint(columnNames = {"image_id", "student_id"})
)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ImageTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id")
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagValue tag;          // what the student answered

    @Column(nullable = false)
    private boolean correct;       // pre-computed: tag == image.correctTag

    @Column(nullable = false, updatable = false)
    private Instant taggedAt;

    @PrePersist
    void onCreate() { this.taggedAt = Instant.now(); }
}
```

### `TagValue` enum

```java
public enum TagValue { DANGEROUS, SAFE }
```

### Repositories

```java
interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByActiveTrue();
    List<Image> findAllByActiveTrueAndCategory(String category);
    List<Image> findAllByUploadedById(Long userId);
}

interface ImageTagRepository extends JpaRepository<ImageTag, Long> {
    Optional<ImageTag> findByImageIdAndStudentId(Long imageId, Long studentId);
    boolean existsByImageIdAndStudentId(Long imageId, Long studentId);

    // All tags a student has submitted
    List<ImageTag> findAllByStudentId(Long studentId);

    // All responses for a given image (teacher views results)
    List<ImageTag> findAllByImageId(Long imageId);

    // Correct answers by a student
    List<ImageTag> findAllByStudentIdAndCorrectTrue(Long studentId);

    // Count of tags submitted by students in a class (for stats)
    @Query("""
        SELECT it FROM ImageTag it
        WHERE it.student.schoolClass.id = :classId
        """)
    List<ImageTag> findAllByStudentClassId(@Param("classId") Long classId);

    // How many images has this student tagged
    long countByStudentId(Long studentId);

    // How many correct tags does this student have
    long countByStudentIdAndCorrectTrue(Long studentId);
}
```

### Test class: `ImageTagRepositoryTest` (`@DataJpaTest`)

| Test | Verifies |
|------|----------|
| `findByImageIdAndStudentId_existing_returns()` | correct entity returned |
| `existsByImageIdAndStudentId_duplicate_returnsTrue()` | unique constraint queryable |
| `findAllByStudentId_multipleImages_returnsAll()` | scope by student |
| `findAllByImageId_multipleStudents_returnsAll()` | scope by image |
| `countByStudentIdAndCorrectTrue_onlyCountsCorrect()` | accuracy check |

---

## Out of Scope

- File upload / blob storage (the `imageUrl` field just stores a string; where that URL comes from is addressed in ISSUE-008)
- Re-tagging / updating a tag (students submit once per image in MVP)
