package spot.safety.ssbackend.model;

import jakarta.persistence.*;
import lombok.*;
import spot.safety.ssbackend.user.User;

import java.time.Instant;

@Entity
@Table(name = "images")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "uploadedBy")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagValue correctTag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

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
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
