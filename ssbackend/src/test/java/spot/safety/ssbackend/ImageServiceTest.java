package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.image.UpdateImageRequest;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.image.ImageServiceImpl;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;
import spot.safety.ssbackend.exception.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageTagRepository imageTagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ImageServiceImpl imageService;

    private School school;
    private User teacher;
    private User admin;
    private User student;
    private Image image;

    @BeforeEach
    void setUp() {
        school = School.builder().id(1L).name("School").build();
        teacher = User.builder().id(10L).username("teacher").role(Role.TEACHER).school(school).active(true).build();
        admin = User.builder().id(11L).username("admin").role(Role.ADMIN).school(school).active(true).build();
        student = User.builder().id(12L).username("student").role(Role.STUDENT).school(school).active(true).build();

        image = Image.builder()
                .id(100L)
                .title("Chemie")
                .description("desc")
                .imageUrl("https://example.test/image.png")
                .category("Chemie")
                .correctTag(TagValue.DANGEROUS)
                .uploadedBy(teacher)
                .active(true)
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-02T00:00:00Z"))
                .build();
    }

    @Test
    void getImage_asStudent_correctTagIsNull() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));

        var response = imageService.getImage(100L, new UserPrincipal(12L, "student", Role.STUDENT, 1L, null));

        assertThat(response.correctTag()).isNull();
    }

    @Test
    void getImage_asTeacher_correctTagPresent() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));

        var response = imageService.getImage(100L, new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null));

        assertThat(response.correctTag()).isEqualTo(TagValue.DANGEROUS);
    }

    @Test
    void updateImage_notOwnerNotAdmin_throwsAccessDenied() {
        Image otherImage = Image.builder().id(200L).uploadedBy(admin).active(true).correctTag(TagValue.SAFE).build();
        when(imageRepository.findById(200L)).thenReturn(Optional.of(otherImage));

        assertThatThrownBy(() -> imageService.updateImage(
                200L,
                new UpdateImageRequest("new", null, null, null, null),
                new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteImage_setsActiveFalse() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        imageService.deleteImage(100L, new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null));

        assertThat(image.isActive()).isFalse();
        verify(imageRepository).save(image);
    }

    @Test
    void createImage_setsUploadedByFromActor() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(teacher));
        when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = imageService.createImage(
                new CreateImageRequest("title", "desc", "https://example.test/1.png", "Chemie", TagValue.SAFE),
                new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null));

        assertThat(response.uploadedById()).isEqualTo(10L);
        verify(imageRepository).save(argThat(saved -> saved.getUploadedBy().getId().equals(10L)));
    }

    @Test
    void getImageResults_returnsMappedResponses() {
        ImageTag tag = ImageTag.builder()
                .id(1L)
                .image(image)
                .student(student)
                .tag(TagValue.SAFE)
                .correct(false)
                .taggedAt(Instant.parse("2026-01-03T00:00:00Z"))
                .build();
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageTagRepository.findAllByImageId(100L)).thenReturn(List.of(tag));

        var results = imageService.getImageResults(100L, new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).username()).isEqualTo("student");
    }
}
