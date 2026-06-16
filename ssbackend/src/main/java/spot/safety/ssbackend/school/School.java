package spot.safety.ssbackend.school;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.dto.RegisterRequest;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.user.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@Data
public class School {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private long id;

   private String name;
   private String licenseKey;
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
        this.expirationDate = LocalDate.now();
        this.expirationDate.plusMonths(12);
        this.licenseStatus = LicenseStatus.ACTIVE;
    }

}
