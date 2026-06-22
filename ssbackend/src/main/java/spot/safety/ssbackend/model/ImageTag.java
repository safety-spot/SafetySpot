package spot.safety.ssbackend.model;

import jakarta.persistence.*;
import lombok.*;
import spot.safety.ssbackend.user.User;

import java.time.Instant;

@Entity
@Table(
        name = "image_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"image_id", "student_id"})
)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"image", "student"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private TagValue tag;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false, updatable = false)
    private Instant taggedAt;

    @PrePersist
    void onCreate() {
        this.taggedAt = Instant.now();
    }
}
