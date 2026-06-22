package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ImageRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private User uploader;

    @BeforeEach
    void setUp() {
        School school = schoolRepository.saveAndFlush(School.builder()
                .name("Image School")
                .licenseStatus(LicenseStatus.ACTIVE)
                .build());

        uploader = userRepository.saveAndFlush(User.builder()
                .username("teacher1")
                .passwordHash("$2a$10$hash")
                .role(Role.TEACHER)
                .school(school)
                .active(true)
                .build());

        imageRepository.saveAndFlush(Image.builder()
                .title("Chemieglas")
                .description("Becherglas am Rand")
                .imageUrl("https://example.test/chemieglas.png")
                .category("Chemie")
                .correctTag(TagValue.DANGEROUS)
                .feedbackCorrect("Richtig")
                .feedbackWrong("Falsch")
                .uploadedBy(uploader)
                .active(true)
                .build());

        imageRepository.saveAndFlush(Image.builder()
                .title("Werkbank")
                .description("Ordentliche Werkbank")
                .imageUrl("https://example.test/werkbank.png")
                .category("Werkraum")
                .correctTag(TagValue.SAFE)
                .feedbackCorrect("Richtig")
                .feedbackWrong("Falsch")
                .uploadedBy(uploader)
                .active(true)
                .build());

        imageRepository.saveAndFlush(Image.builder()
                .title("Inaktiv")
                .description("Versteckt")
                .imageUrl("https://example.test/inaktiv.png")
                .category("Chemie")
                .correctTag(TagValue.SAFE)
                .feedbackCorrect("Richtig")
                .feedbackWrong("Falsch")
                .uploadedBy(uploader)
                .active(false)
                .build());
    }

    @Test
    void findAllByActiveTrue_returnsOnlyActiveImages() {
        List<Image> images = imageRepository.findAllByActiveTrue();

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(Image::isActive);
    }

    @Test
    void findAllByActiveTrueAndCategory_returnsOnlyMatchingCategory() {
        List<Image> images = imageRepository.findAllByActiveTrueAndCategory("Chemie");

        assertThat(images).hasSize(1);
        assertThat(images.get(0).getTitle()).isEqualTo("Chemieglas");
    }

    @Test
    void findAllByUploadedById_returnsOnlyUploaderImages() {
        List<Image> images = imageRepository.findAllByUploadedById(uploader.getId());

        assertThat(images).hasSize(3);
    }

    @Test
    void countByActiveTrue_returnsActiveCount() {
        assertThat(imageRepository.countByActiveTrue()).isEqualTo(2);
    }
}
