package spot.safety.ssbackend.school;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.enums.LicenseStatus;

import java.time.LocalDate;


@Entity
@Data
public class School {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private long id;

   private String name;
   private String licenseKey;
   @Enumerated(EnumType.STRING)
   private LicenseStatus licenseStatus;
   private LocalDate expirationDate;


    public School(String name, String licenseKey) {
        this.name = name;
        this.licenseKey = licenseKey;
        this.licenseStatus = LicenseStatus.INACTIVE;
    }

    public School() { }

    public void activateLicense(String key) {
        this.licenseKey = key;
        this.expirationDate = LocalDate.now().plusMonths(12);
        this.licenseStatus = LicenseStatus.ACTIVE;
    }

}
