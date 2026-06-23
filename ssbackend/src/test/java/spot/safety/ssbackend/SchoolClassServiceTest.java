package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spot.safety.ssbackend.dto.school.UpdateSchoolClass;
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.exception.UsernameAlreadyTakenException;
import spot.safety.ssbackend.school.*;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private SchoolService schoolService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SchoolClassService schoolClassService;

    private School sampleSchool() {
        return School.builder().id(1L).name("Sample School").build();
    }

    private SecurityUser adminPrincipal() {
        return new SecurityUser(User.builder()
                .id(1L).username("admin").role(Role.ADMIN).active(true).build());
    }

    private SecurityUser teacherPrincipal() {
        return new SecurityUser(User.builder()
                .id(2L).username("teacher").role(Role.TEACHER).active(true).build());
    }

    // newClass

    @Test
    void newClass_createsAndSavesClass() {
        when(schoolService.getSchoolById(1L)).thenReturn(sampleSchool());
        when(schoolClassRepository.findByName("Class A")).thenReturn(Optional.empty());

        SchoolClass result = schoolClassService.newClass(1L, "Class A");

        assertThat(result.getName()).isEqualTo("Class A");
        assertThat(result.getSchool().getId()).isEqualTo(1L);
        verify(schoolClassRepository).save(result);
    }

    @Test
    void newClass_duplicateName_throws() {
        when(schoolService.getSchoolById(1L)).thenReturn(sampleSchool());
        when(schoolClassRepository.findByName("Class A")).thenReturn(Optional.of(new SchoolClass()));

        assertThatThrownBy(() -> schoolClassService.newClass(1L, "Class A"))
                .isInstanceOf(DuplicateTagException.class);

        verify(schoolClassRepository, never()).save(any());
    }

    // getClasses

    @Test
    void getClasses_asAdmin_returnsAllClasses() {
        List<SchoolClass> classes = List.of(new SchoolClass(), new SchoolClass());
        when(schoolClassRepository.findAll()).thenReturn(classes);

        List<SchoolClass> result = schoolClassService.getClasses(adminPrincipal());

        assertThat(result).isEqualTo(classes);
        verify(schoolClassRepository, never()).findSchoolClassByTeacher_Id(any());
    }

    @Test
    void getClasses_asTeacher_returnsOwnClasses() {
        SchoolClass teacherClass = new SchoolClass();
        when(schoolClassRepository.findSchoolClassByTeacher_Id(2L)).thenReturn(teacherClass);

        List<SchoolClass> result = schoolClassService.getClasses(teacherPrincipal());

        assertThat(result).containsExactly(teacherClass);
        verify(schoolClassRepository, never()).findAll();
    }

    // getAmountOfStudent

    @Test
    void getAmountOfStudent_returnsSize() {
        when(userService.getUsers(eq(1L), any())).thenReturn(List.of(mock(UserResponse.class), mock(UserResponse.class)));

        int count = schoolClassService.getAmountOfStudent(1L, teacherPrincipal());

        assertThat(count).isEqualTo(2);
    }

    // getStudents

    @Test
    void getStudents_returnsUserList() {
        List<UserResponse> students = List.of(mock(UserResponse.class));
        when(userService.getUsers(eq(1L), any())).thenReturn(students);

        List<UserResponse> result = schoolClassService.getStudents(1L, teacherPrincipal());

        assertThat(result).isEqualTo(students);
    }

    // updateClass

    @Test
    void updateClass_withName_updatesNameAndSaves() {
        SchoolClass existing = SchoolClass.builder().id(1L).name("Old Name").build();
        when(schoolClassRepository.findSchoolClassesById(1L)).thenReturn(existing);

        UpdateSchoolClass request = new UpdateSchoolClass("New Name", null);

        schoolClassService.updateClass(1L, teacherPrincipal(), request);

        assertThat(existing.getName()).isEqualTo("New Name");
        verify(schoolClassRepository).save(existing);
        verify(userService, never()).getUserById(any(), any());
    }

    @Test
    void updateClass_withTeacherId_updatesTeacherAndSaves() {
        SchoolClass existing = SchoolClass.builder().id(1L).name("Class A").build();
        when(schoolClassRepository.findSchoolClassesById(1L)).thenReturn(existing);

        UserResponse ur = new UserResponse(5L, "newteacher", Role.TEACHER, 1L, null, true,
                Instant.parse("2026-01-01T00:00:00Z"), null);
        when(userService.getUserById(eq(5L), any())).thenReturn(ur);

        User teacherEntity = User.builder().id(5L).username("newteacher").role(Role.TEACHER).active(true).build();
        when(userService.findByUsername("newteacher")).thenReturn(teacherEntity);

        UpdateSchoolClass request = new UpdateSchoolClass(null, 5L);

        schoolClassService.updateClass(1L, adminPrincipal(), request);

        assertThat(existing.getTeacher()).isEqualTo(teacherEntity);
        verify(schoolClassRepository).save(existing);
    }

    // deleteClass

    @Test
    void deleteClass_noStudents_deletesClass() {
        when(userService.getUsers(eq(1L), any())).thenReturn(List.of());
        SchoolClass existing = SchoolClass.builder().id(1L).build();
        when(schoolClassRepository.findSchoolClassesById(1L)).thenReturn(existing);

        schoolClassService.deleteClass(1L, adminPrincipal());

        verify(schoolClassRepository).delete(existing);
    }

    @Test
    void deleteClass_hasStudents_throwsAndDoesNotDelete() {
        when(userService.getUsers(eq(1L), any())).thenReturn(List.of(mock(UserResponse.class)));

        assertThatThrownBy(() -> schoolClassService.deleteClass(1L, adminPrincipal()))
                .isInstanceOf(UsernameAlreadyTakenException.class);

        verify(schoolClassRepository, never()).delete(any());
    }
}