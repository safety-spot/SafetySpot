package spot.safety.ssbackend.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.dto.school.UpdateSchoolRequest;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.user.SecurityUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchoolService {
    private final SchoolRepository schoolRepository;

    public School getSchoolByName(String name) {
        Optional<School> school = schoolRepository.findByName(name);
        if(school.isEmpty()) {
            throw new EntityNotFoundException("School: " + name + " was not found in database!");
        }

        return school.get();
    }

    public void newSchool(School school) {
        schoolRepository.save(school);
    }

    public School getSchoolById(long id) {
        Optional<School> school = schoolRepository.findById(id);
        if (school.isEmpty())
            throw new EntityNotFoundException("School with id: " + id + " not found!");

        return school.get();
    }

    public List<School> getAllSchools () {
        return schoolRepository.findAll();
    }

    public void updateSchool(Integer id, UpdateSchoolRequest request) {
        School school = schoolRepository.getSchoolById(id.longValue());

        school.activateLicense(request.licenseKey());
        school.setName(request.name());
    }
}
