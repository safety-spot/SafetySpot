package spot.safety.ssbackend.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.dto.school.CreateSchool;
import spot.safety.ssbackend.dto.school.UpdateSchoolRequest;
import spot.safety.ssbackend.exception.AccessDeniedException;
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

    public School getSchoolById(long id) {
        Optional<School> school = schoolRepository.findById(id);
        if (school.isEmpty())
            throw new EntityNotFoundException("School with id: " + id + " not found!");

        return school.get();
    }

    public List<School> getAllSchools () {
        return schoolRepository.findAll();
    }

    public void updateSchool(long id, UpdateSchoolRequest request) {
        School school = getSchoolById(id);

        if (request.licenseKey() != null) {
            school.activateLicense(request.licenseKey());
        }
        if (request.name() != null) {
            school.setName(request.name());
        }

        schoolRepository.save(school);
    }

    public void activateLicense(long id, String key, SecurityUser principal) {
        School school = getSchoolById(id);
        if(id != principal.getUser().getSchool().getId()) {
            throw new AccessDeniedException("User does not have access to this school");
        }
        school.activateLicense(key);
        schoolRepository.save(school);
    }

    public School createSchool(CreateSchool reqSchool) {
        School school = new School();
        school.setName(reqSchool.name());
        school.activateLicense(reqSchool.licenseKey());

        schoolRepository.save(school);
        return school;
    }
}
