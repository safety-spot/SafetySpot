package spot.safety.ssbackend.dto.school;

import jakarta.validation.constraints.Size;

public record UpdateSchoolRequest(
        @Size(max = 100) String name,
        @Size(max = 255) String licenseKey
) {
}
