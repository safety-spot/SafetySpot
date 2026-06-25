package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.progress.ProgressServiceImpl;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.stats.StatsServiceImpl;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageTagRepository imageTagRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private School school;
    private SchoolClass schoolClass;
    private User teacher;
    private User admin;
    private User studentOne;
    private User studentTwo;
    private Image image;

    @BeforeEach
    void setUp() {
        school = School.builder().id(1L).name("School").build();
        teacher = User.builder().id(10L).username("teacher").role(Role.TEACHER).school(school).active(true).build();
        admin = User.builder().id(11L).username("admin").role(Role.ADMIN).school(school).active(true).build();
        schoolClass = SchoolClass.builder().id(20L).name("8A").school(school).teacher(teacher).build();
        studentOne = User.builder().id(30L).username("anna").role(Role.STUDENT).school(school).schoolClass(schoolClass).active(true).build();
        studentTwo = User.builder().id(31L).username("berta").role(Role.STUDENT).school(school).schoolClass(schoolClass).active(true).build();
        image = Image.builder()
                .id(100L)
                .title("Image")
                .correctTag(TagValue.DANGEROUS)
                .feedbackCorrect("ok")
                .feedbackWrong("bad")
                .uploadedBy(teacher)
                .active(true)
                .build();
    }

    @Test
    void getClassStats_asTeacher_wrongClass_throwsAccessDenied() {
        SchoolClass otherClass = SchoolClass.builder().id(99L).name("9B").school(school).teacher(User.builder().id(99L).username("other").role(Role.TEACHER).school(school).active(true).build()).build();
        when(schoolClassRepository.findById(99L)).thenReturn(Optional.of(otherClass));

        assertThatThrownBy(() -> statsService.getClassStats(99L, new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getImageStats_computesCorrectRateFromTags() {
        when(imageRepository.findById(100L)).thenReturn(Optional.of(image));
        when(imageTagRepository.findAllByImageId(100L)).thenReturn(List.of(
                ImageTag.builder().image(image).student(studentOne).tag(TagValue.DANGEROUS).correct(true).taggedAt(Instant.now()).build(),
                ImageTag.builder().image(image).student(studentTwo).tag(TagValue.DANGEROUS).correct(true).taggedAt(Instant.now()).build(),
                ImageTag.builder().image(image).student(studentTwo).tag(TagValue.SAFE).correct(false).taggedAt(Instant.now()).build()
        ));

        var response = statsService.getImageStats(100L, new UserPrincipal(10L, "teacher", Role.TEACHER, 1L, null));

        assertThat(response.totalResponses()).isEqualTo(3);
        assertThat(response.correctResponses()).isEqualTo(2);
        assertThat(response.correctRate()).isEqualTo(2.0 / 3.0);
        assertThat(response.dangerousCount()).isEqualTo(2);
        assertThat(response.safeCount()).isEqualTo(1);
    }
}
