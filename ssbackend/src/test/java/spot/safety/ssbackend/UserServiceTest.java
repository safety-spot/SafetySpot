package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import spot.safety.ssbackend.dto.user.CreateUserRequest;
import spot.safety.ssbackend.dto.user.ResetPasswordRequest;
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.UsernameAlreadyTakenException;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.school.SchoolRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;
import spot.safety.ssbackend.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    SchoolClassRepository schoolClassRepository;
    @Mock
    SchoolRepository schoolRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private School school;
    private SchoolClass schoolClass;
    private UserPrincipal teacherPrincipal;
    private UserPrincipal adminPrincipal;

    @BeforeEach
    void setUp() {
        school = School.builder().id(1L).name("Test School").build();
        schoolClass = SchoolClass.builder().id(10L).name("10A").school(school).build();

        User teacherUser = User.builder().id(2L).username("teacher1").role(Role.TEACHER).school(school).build();
        schoolClass.setTeacher(teacherUser);

        teacherPrincipal = new UserPrincipal(2L, "teacher1", Role.TEACHER, 1L, null);
        adminPrincipal = new UserPrincipal(3L, "admin1", Role.ADMIN, 1L, null);
    }

    @Test
    void createUser_asTeacher_hashesPassword() {
        when(userRepository.existsByUsername("student1")).thenReturn(false);
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolClassRepository.findById(10L)).thenReturn(Optional.of(schoolClass));
        when(passwordEncoder.encode("rawpass")).thenReturn("$2a$10$hash");
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return User.builder()
                    .id(99L).username(u.getUsername()).passwordHash(u.getPasswordHash())
                    .role(u.getRole()).school(u.getSchool()).schoolClass(u.getSchoolClass()).active(true).build();
        });

        UserResponse response = userService.createUser(
                new CreateUserRequest("student1", "rawpass", Role.STUDENT, 10L),
                teacherPrincipal);

        assertThat(response.username()).isEqualTo("student1");
        verify(passwordEncoder).encode("rawpass");
        verify(userRepository).save(argThat(u -> u.getPasswordHash().equals("$2a$10$hash")));
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(
                new CreateUserRequest("taken", "pass123", Role.STUDENT, null),
                adminPrincipal))
                .isInstanceOf(UsernameAlreadyTakenException.class);
    }

    @Test
    void createUser_teacherEscalatesRole_throwsAccessDenied() {
        assertThatThrownBy(() -> userService.createUser(
                new CreateUserRequest("newteacher", "pass123", Role.TEACHER, null),
                teacherPrincipal))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("STUDENT");
    }

    @Test
    void deactivateUser_setsActiveFalse() {
        User target = User.builder().id(5L).username("student1").role(Role.STUDENT)
                .school(school).schoolClass(schoolClass).active(true).build();
        schoolClass.setTeacher(User.builder().id(2L).build());
        when(userRepository.findById(5L)).thenReturn(Optional.of(target));
        when(schoolClassRepository.findAllByTeacherId(2L)).thenReturn(List.of(schoolClass));

        userService.deactivateUser(5L, teacherPrincipal);

        verify(userRepository).save(argThat(u -> !u.isActive()));
    }

    @Test
    void resetPassword_storesNewHash() {
        User target = User.builder().id(5L).username("student1").role(Role.STUDENT)
                .school(school).schoolClass(schoolClass).active(true).build();
        schoolClass.setTeacher(User.builder().id(2L).build());
        when(userRepository.findById(5L)).thenReturn(Optional.of(target));
        when(schoolClassRepository.findAllByTeacherId(2L)).thenReturn(List.of(schoolClass));
        when(passwordEncoder.encode("newpass1")).thenReturn("$2a$10$newhash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.resetPassword(5L, new ResetPasswordRequest("newpass1"), teacherPrincipal);

        verify(userRepository).save(argThat(u -> u.getPasswordHash().equals("$2a$10$newhash")));
    }

    @Test
    void getUsers_asTeacher_onlyOwnClassReturned() {
        SchoolClass otherClass = SchoolClass.builder().id(20L).name("11B").school(school).build();
        User s1 = User.builder().id(10L).username("s1").role(Role.STUDENT).school(school).schoolClass(schoolClass).active(true).build();

        when(schoolClassRepository.findAllByTeacherId(2L)).thenReturn(List.of(schoolClass));
        when(userRepository.findAllBySchoolClassId(10L)).thenReturn(List.of(s1));

        List<UserResponse> result = userService.getUsers(null, teacherPrincipal);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("s1");
    }
}
