package spot.safety.ssbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.school.School;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findBySchool(School school);
    List<User> findByUserRole(UserRole userRole);
    List<User> findAllBySchoolId(Long schoolId);
}
