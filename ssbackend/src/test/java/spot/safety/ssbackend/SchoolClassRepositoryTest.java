package spot.safety.ssbackend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.school.SchoolRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
class SchoolClassRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    private School school;
    private School otherSchool;

    @BeforeEach
    void setUp() {
        school = schoolRepository.saveAndFlush(School.builder()
                .name("School A")
                .licenseStatus(LicenseStatus.ACTIVE)
                .build());

        otherSchool = schoolRepository.saveAndFlush(School.builder()
                .name("School B")
                .licenseStatus(LicenseStatus.ACTIVE)
                .build());

        schoolClassRepository.saveAndFlush(SchoolClass.builder()
                .name("10A")
                .school(school)
                .build());

        schoolClassRepository.saveAndFlush(SchoolClass.builder()
                .name("10B")
                .school(school)
                .build());

        schoolClassRepository.saveAndFlush(SchoolClass.builder()
                .name("10A")
                .school(otherSchool)
                .build());
    }

    @Test
    void existsByNameAndSchoolId_duplicateName_returnsTrue() {
        boolean exists = schoolClassRepository.existsByNameAndSchoolId("10A", school.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void findAllBySchoolId_returnsOnlySchoolClasses() {
        List<SchoolClass> classes = schoolClassRepository.findAllBySchoolId(school.getId());

        assertThat(classes).hasSize(2);
        assertThat(classes).allMatch(c -> c.getSchool().getId().equals(school.getId()));
    }
}
