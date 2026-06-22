package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spot.safety.ssbackend.dto.tag.SubmitTagRequest;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;
import spot.safety.ssbackend.tag.TagServiceImpl;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageTagRepository imageTagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private User student;
    private Image image;

    @BeforeEach
    void setUp() {
        School school = School.builder().id(1L).name("School").build();
        student = User.builder().id(10L).username("student").role(Role.STUDENT).school(school).active(true).build();
        image = Image.builder()
                .id(100L)
                .title("Image")
                .imageUrl("https://example.test/image.png")
                .correctTag(TagValue.DANGEROUS)
                .feedbackCorrect("Richtig")
                .feedbackWrong("Falsch")
                .uploadedBy(User.builder().id(99L).username("teacher").role(Role.TEACHER).school(school).active(true).build())
                .active(true)
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-02T00:00:00Z"))
                .build();
    }

    @Test
    void submitTag_correctAnswer_returnsCorrectTrue() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageTagRepository.existsByImageIdAndStudentId(100L, 10L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));
        when(imageTagRepository.save(any())).thenAnswer(inv -> {
            ImageTag tag = inv.getArgument(0);
            tag.setTaggedAt(Instant.parse("2026-01-03T00:00:00Z"));
            return tag;
        });

        var response = tagService.submitTag(100L, new SubmitTagRequest(TagValue.DANGEROUS),
                new UserPrincipal(10L, "student", Role.STUDENT, 1L, null));

        assertThat(response.correct()).isTrue();
        assertThat(response.feedback()).isEqualTo("Richtig");
    }

    @Test
    void submitTag_wrongAnswer_returnsCorrectFalse() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageTagRepository.existsByImageIdAndStudentId(100L, 10L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));
        when(imageTagRepository.save(any())).thenAnswer(inv -> {
            ImageTag tag = inv.getArgument(0);
            tag.setTaggedAt(Instant.parse("2026-01-03T00:00:00Z"));
            return tag;
        });

        var response = tagService.submitTag(100L, new SubmitTagRequest(TagValue.SAFE),
                new UserPrincipal(10L, "student", Role.STUDENT, 1L, null));

        assertThat(response.correct()).isFalse();
        assertThat(response.feedback()).isEqualTo("Falsch");
    }

    @Test
    void submitTag_duplicate_throwsDuplicateTagException() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageTagRepository.existsByImageIdAndStudentId(100L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> tagService.submitTag(100L, new SubmitTagRequest(TagValue.SAFE),
                new UserPrincipal(10L, "student", Role.STUDENT, 1L, null)))
                .isInstanceOf(DuplicateTagException.class);
    }

    @Test
    void submitTag_inactiveImage_throwsEntityNotFound() {
        Image inactive = Image.builder()
                .id(101L)
                .title("Image")
                .imageUrl("https://example.test/image.png")
                .correctTag(TagValue.DANGEROUS)
                .feedbackCorrect("Richtig")
                .feedbackWrong("Falsch")
                .uploadedBy(image.getUploadedBy())
                .active(false)
                .build();

        when(imageRepository.findById(101L)).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> tagService.submitTag(101L, new SubmitTagRequest(TagValue.SAFE),
                new UserPrincipal(10L, "student", Role.STUDENT, 1L, null)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
