package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.school.SchoolRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class ImageTagRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageTagRepository imageTagRepository;

    private User student;
    private User otherStudent;
    private Image imageOne;
    private Image imageTwo;

    @BeforeEach
    void setUp() {
        School school = schoolRepository.saveAndFlush(School.builder()
                .name("Tag School")
                .licenseStatus(LicenseStatus.ACTIVE)
                .build());

        SchoolClass schoolClass = schoolClassRepository.saveAndFlush(SchoolClass.builder()
                .name("8A")
                .school(school)
                .build());

        student = userRepository.saveAndFlush(User.builder()
                .username("student1")
                .passwordHash("$2a$10$hash")
                .role(Role.STUDENT)
                .school(school)
                .schoolClass(schoolClass)
                .active(true)
                .build());

        otherStudent = userRepository.saveAndFlush(User.builder()
                .username("student2")
                .passwordHash("$2a$10$hash2")
                .role(Role.STUDENT)
                .school(school)
                .schoolClass(schoolClass)
                .active(true)
                .build());

        User uploader = userRepository.saveAndFlush(User.builder()
                .username("teacher1")
                .passwordHash("$2a$10$hash3")
                .role(Role.TEACHER)
                .school(school)
                .active(true)
                .build());

        imageOne = imageRepository.saveAndFlush(Image.builder()
                .title("Image 1")
                .imageUrl("https://example.test/1.png")
                .correctTag(TagValue.DANGEROUS)
                .uploadedBy(uploader)
                .active(true)
                .build());

        imageTwo = imageRepository.saveAndFlush(Image.builder()
                .title("Image 2")
                .imageUrl("https://example.test/2.png")
                .correctTag(TagValue.SAFE)
                .uploadedBy(uploader)
                .active(true)
                .build());

        imageTagRepository.saveAndFlush(ImageTag.builder()
                .image(imageOne)
                .student(student)
                .tag(TagValue.DANGEROUS)
                .correct(true)
                .build());

        imageTagRepository.saveAndFlush(ImageTag.builder()
                .image(imageTwo)
                .student(student)
                .tag(TagValue.DANGEROUS)
                .correct(false)
                .build());

        imageTagRepository.saveAndFlush(ImageTag.builder()
                .image(imageOne)
                .student(otherStudent)
                .tag(TagValue.SAFE)
                .correct(false)
                .build());
    }

    @Test
    void findByImageIdAndStudentId_existing_returns() {
        assertThat(imageTagRepository.findByImageIdAndStudentId(imageOne.getId(), student.getId())).isPresent();
    }

    @Test
    void existsByImageIdAndStudentId_duplicate_returnsTrue() {
        assertThat(imageTagRepository.existsByImageIdAndStudentId(imageOne.getId(), student.getId())).isTrue();
    }

    @Test
    void findAllByStudentId_multipleImages_returnsAll() {
        List<ImageTag> tags = imageTagRepository.findAllByStudentId(student.getId());

        assertThat(tags).hasSize(2);
    }

    @Test
    void findAllByImageId_multipleStudents_returnsAll() {
        List<ImageTag> tags = imageTagRepository.findAllByImageId(imageOne.getId());

        assertThat(tags).hasSize(2);
    }

    @Test
    void countByStudentIdAndCorrectTrue_onlyCountsCorrect() {
        assertThat(imageTagRepository.countByStudentIdAndCorrectTrue(student.getId())).isEqualTo(1);
    }

    @Test
    void findAllByStudentClassId_returnsClassResponses() {
        List<ImageTag> tags = imageTagRepository.findAllByStudentClassId(student.getSchoolClass().getId());

        assertThat(tags).hasSize(3);
    }
}
