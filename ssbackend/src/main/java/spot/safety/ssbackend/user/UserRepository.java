package spot.safety.ssbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.safety.ssbackend.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findAllBySchoolClassId(Long classId);
    List<User> findAllBySchoolId(Long schoolId);
    List<User> findAllBySchoolIdAndRole(Long schoolId, Role role);
}
