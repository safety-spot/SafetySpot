package spot.safety.ssbackend.user;

import jakarta.persistence.Entity;
import lombok.Data;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.school.School;

@Entity
public class Teacher extends User{

    public Teacher(String username, String pwdHash, School school, UserRole userRole) {
        super(username, pwdHash, school, userRole);
    }

    public Teacher() {
    }
}
