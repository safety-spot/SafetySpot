package spot.safety.ssbackend.user;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.school.School;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Student extends User{

   private int totalPoints;
   private int level;
   private int streakDays;
   private LocalDate lastPlayedScenario;

    public Student(String username, String pwdHash, School school, UserRole userRole) {
        super(username, pwdHash, school, userRole);
        this.totalPoints = 0;
        this.level = 1;
        this.streakDays = 0;
        this.lastPlayedScenario = LocalDate.now();
    }

    public Student() {
        super();
    }


}
