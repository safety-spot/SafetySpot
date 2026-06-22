package spot.safety.ssbackend.user;

import spot.safety.ssbackend.enums.Role;

public record UserPrincipal(Long id, String username, Role role, Long schoolId, Long schoolClassId) {

    public static UserPrincipal from(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getSchool().getId(),
                user.getSchoolClass() != null ? user.getSchoolClass().getId() : null
        );
    }
}
