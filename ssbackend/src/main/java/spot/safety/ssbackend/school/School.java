package spot.safety.ssbackend.school;

import jakarta.persistence.*;
import lombok.*;
import spot.safety.ssbackend.enums.LicenseStatus;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "schools")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String licenseKey;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LicenseStatus licenseStatus = LicenseStatus.INACTIVE;

    private LocalDate licenseExpiry;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }

    public void activateLicense(String key) {
        this.licenseKey = key;
        this.licenseExpiry = LocalDate.now().plusMonths(12);
        this.licenseStatus = LicenseStatus.ACTIVE;
    }

}
