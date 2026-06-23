package spot.safety.ssbackend.dto.school;

public record UpdateSchoolRequest(
        String name,
        String licenseKey
) {
}
