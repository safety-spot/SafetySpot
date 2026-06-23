package spot.safety.ssbackend.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllBySchoolId(Long schoolId);
    List<SchoolClass> findAllByTeacherId(Long teacherId);

    Optional<SchoolClass> findByName(String name);
    boolean existsByNameAndSchoolId(String name, Long schoolId);
}
