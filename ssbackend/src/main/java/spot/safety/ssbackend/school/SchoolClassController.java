package spot.safety.ssbackend.school;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.safety.ssbackend.dto.school.CreateClass;
import spot.safety.ssbackend.dto.school.UpdateSchoolClass;
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.user.SecurityUser;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class SchoolClassController {
    private final SchoolClassService schoolClassService;

    // POST

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<SchoolClass> newClass(
            @RequestBody @Valid CreateClass reqClass) {

       SchoolClass schoolClass = schoolClassService.newClass(reqClass.schoolId(), reqClass.name());
        return ResponseEntity.status(HttpStatus.OK)
                .body(schoolClass);
    }

    // GET
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<List<SchoolClass>> getClasses(
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassService.getClasses(principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<Integer> getStudentCount(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassService.getAmountOfStudent(id, principal));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<List<UserResponse>> getStudents (
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(schoolClassService.getStudents(id, principal));
    }

    // PUT

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<String> updateClass (
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal,
            @RequestBody @Valid UpdateSchoolClass request
            ) {

        schoolClassService.updateClass(id, principal, request);
        return ResponseEntity
                .ok("All good");
    }

    // DELETE

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteClass(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser principal) {
        schoolClassService.deleteClass(id, principal);
        return ResponseEntity.ok("All good");
    }
}
