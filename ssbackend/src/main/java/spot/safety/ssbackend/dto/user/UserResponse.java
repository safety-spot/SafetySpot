package spot.safety.ssbackend.dto.user;

import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.user.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        Role role,
        Long schoolId,
        Long classId,
        boolean active,
        Instant createdAt,
        Instant lastLoginAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getSchool().getId(),
                user.getSchoolClass() != null ? user.getSchoolClass().getId() : null,
                user.isActive(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
