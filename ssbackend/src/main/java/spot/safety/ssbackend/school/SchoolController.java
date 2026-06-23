package spot.safety.ssbackend.school;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spot.safety.ssbackend.dto.school.CreateSchool;
import spot.safety.ssbackend.dto.school.UpdateSchoolRequest;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;
    private final SchoolClassService classGroupService;

    // GET

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<List<School>> getAllSchools(
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolService.getAllSchools());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<School> getSchoolById(@PathVariable int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolService.getSchoolById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> updateSchool(
            @PathVariable long id,
            @RequestBody @Valid UpdateSchoolRequest request
            ) {
        schoolService.updateSchool(id, request);
        return ResponseEntity.ok("All good");
    }

    // POST

    @PostMapping("/{id}/activate-license")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<String> activateLicense(
            @PathVariable long id,
            @RequestBody @Valid String licenseKey) {
        schoolService.activateLicense(id, licenseKey);
        return ResponseEntity.ok("All good");
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<School> newSchool (
            @RequestBody @Valid CreateSchool reqSchool) {
        School school = schoolService.createSchool(reqSchool);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(school);
    }
}
