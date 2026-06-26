package spot.safety.ssbackend.dto.school;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClass(
        long schoolId,
        @NotBlank @Size(max = 50) String name
) {
}
