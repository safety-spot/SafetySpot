package spot.safety.ssbackend.user;

import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.school.School;

public class Student extends User{
    public Student(String username, String pwdHash, School school, UserRole userRole) {
        super(username, pwdHash, school, userRole);
    }
}
