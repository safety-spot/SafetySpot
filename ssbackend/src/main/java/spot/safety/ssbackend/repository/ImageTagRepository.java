package spot.safety.ssbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.safety.ssbackend.model.ImageTag;

import java.util.List;
import java.util.Optional;

public interface ImageTagRepository extends JpaRepository<ImageTag, Long> {
    Optional<ImageTag> findByImageIdAndStudentId(Long imageId, Long studentId);

    boolean existsByImageIdAndStudentId(Long imageId, Long studentId);

    List<ImageTag> findAllByStudentId(Long studentId);

    List<ImageTag> findAllByImageId(Long imageId);

    List<ImageTag> findAllByStudentIdAndCorrectTrue(Long studentId);

    @Query("""
            SELECT it FROM ImageTag it
            WHERE it.student.schoolClass.id = :classId
            """)
    List<ImageTag> findAllByStudentClassId(@Param("classId") Long classId);

    long countByStudentId(Long studentId);

    long countByStudentIdAndCorrectTrue(Long studentId);
}
