package spot.safety.ssbackend.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    List<SchoolClass> findAllBySchoolId(Long schoolId);
    List<SchoolClass> findAllByTeacherId(Long teacherId);
    boolean existsByNameAndSchoolId(String name, Long schoolId);
}
