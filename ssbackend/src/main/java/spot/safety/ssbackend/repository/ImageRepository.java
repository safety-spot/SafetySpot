package spot.safety.ssbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.safety.ssbackend.model.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByActiveTrue();
    long countByActiveTrue();
    List<Image> findAllByActiveTrueAndCategory(String category);
    List<Image> findAllByUploadedById(Long userId);
}
