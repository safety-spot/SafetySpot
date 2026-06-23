package spot.safety.ssbackend.school;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.safety.ssbackend.dto.school.CreateClass;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class SchoolClassController {
    private final SchoolClassService schoolClassService;

    // POST

    @GetMapping
    public ResponseEntity<SchoolClass> newClass(
            @RequestBody @Valid CreateClass reqClass) {

       SchoolClass schoolClass = schoolClassService.newClass(reqClass.schoolId(), reqClass.name());
        return ResponseEntity.status(HttpStatus.OK)
                .body(schoolClass);
    }

    // GET

    // PUT

    // DELETE
}
