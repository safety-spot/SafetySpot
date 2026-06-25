package spot.safety.ssbackend.dto.school;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSchool(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String licenseKey
) {
}
