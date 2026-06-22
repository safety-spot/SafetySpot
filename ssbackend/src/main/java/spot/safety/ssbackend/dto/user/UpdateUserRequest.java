package spot.safety.ssbackend.dto.user;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50) String username,
        Long classId,
        Boolean active
) {}
