package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    private School school;
    private SchoolClass schoolClass;
    private User student;

    @BeforeEach
    void setUp() {
        school = em.persistAndFlush(School.builder()
                .name("Test School")
                .licenseStatus(LicenseStatus.ACTIVE)
                .build());

        schoolClass = em.persistAndFlush(SchoolClass.builder()
                .name("10A")
                .school(school)
                .build());

        student = em.persistAndFlush(User.builder()
                .username("alice")
                .passwordHash("$2a$10$hashedpassword")
                .role(Role.STUDENT)
                .school(school)
                .schoolClass(schoolClass)
                .active(true)
                .build());
    }

    @Test
    void findByUsername_existingUser_returnsUser() {
        Optional<User> result = userRepository.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsername_missingUser_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByUsername_taken_returnsTrue() {
        boolean exists = userRepository.existsByUsername("alice");

        assertThat(exists).isTrue();
    }

    @Test
    void findAllBySchoolClassId_returnsOnlyClassMembers() {
        User otherStudent = em.persistAndFlush(User.builder()
                .username("bob")
                .passwordHash("$2a$10$hashedpassword2")
                .role(Role.STUDENT)
                .school(school)
                .active(true)
                .build());

        List<User> members = userRepository.findAllBySchoolClassId(schoolClass.getId());

        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo("alice");
    }
}
