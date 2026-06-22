package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spot.safety.ssbackend.dto.progress.ProgressEntryResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.progress.ProgressServiceImpl;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ImageTagRepository imageTagRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private UserPrincipal student;
    private Image imageOne;
    private Image imageTwo;

    @BeforeEach
    void setUp() {
        School school = School.builder().id(1L).name("School").build();
        student = new UserPrincipal(10L, "student", Role.STUDENT, 1L, 2L);

        imageOne = Image.builder()
                .id(100L)
                .title("Image 1")
                .category("Chemie")
                .correctTag(TagValue.DANGEROUS)
                .feedbackCorrect("ok")
                .feedbackWrong("bad")
                .uploadedBy(User.builder().id(1L).username("teacher").role(Role.TEACHER).school(school).active(true).build())
                .active(true)
                .build();

        imageTwo = Image.builder()
                .id(200L)
                .title("Image 2")
                .category("Werkraum")
                .correctTag(TagValue.SAFE)
                .feedbackCorrect("ok")
                .feedbackWrong("bad")
                .uploadedBy(User.builder().id(1L).username("teacher").role(Role.TEACHER).school(school).active(true).build())
                .active(true)
                .build();
    }

    @Test
    void getSummary_noTagsYet_returnsZeroAccuracy() {
        when(imageTagRepository.countByStudentId(10L)).thenReturn(0L);
        when(imageTagRepository.countByStudentIdAndCorrectTrue(10L)).thenReturn(0L);

        var summary = progressService.getSummary(student);

        assertThat(summary.totalTagged()).isZero();
        assertThat(summary.correctCount()).isZero();
        assertThat(summary.accuracyPercent()).isZero();
    }

    @Test
    void getSummary_allCorrect_returns100Percent() {
        when(imageTagRepository.countByStudentId(10L)).thenReturn(2L);
        when(imageTagRepository.countByStudentIdAndCorrectTrue(10L)).thenReturn(2L);

        var summary = progressService.getSummary(student);

        assertThat(summary.accuracyPercent()).isEqualTo(100.0);
    }

    @Test
    void getHistory_returnsSortedDescending() {
        ImageTag older = ImageTag.builder()
                .image(imageOne)
                .student(User.builder().id(10L).username("student").role(Role.STUDENT).school(School.builder().id(1L).name("School").build()).active(true).build())
                .tag(TagValue.DANGEROUS)
                .correct(true)
                .taggedAt(Instant.parse("2026-01-01T10:00:00Z"))
                .build();
        ImageTag newer = ImageTag.builder()
                .image(imageTwo)
                .student(User.builder().id(10L).username("student").role(Role.STUDENT).school(School.builder().id(1L).name("School").build()).active(true).build())
                .tag(TagValue.SAFE)
                .correct(true)
                .taggedAt(Instant.parse("2026-01-02T10:00:00Z"))
                .build();

        when(imageTagRepository.findAllByStudentId(10L)).thenReturn(List.of(older, newer));

        List<ProgressEntryResponse> history = progressService.getHistory(student);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).imageId()).isEqualTo(200L);
        assertThat(history.get(1).imageId()).isEqualTo(100L);
    }
}
