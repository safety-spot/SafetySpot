package spot.safety.ssbackend.dto;

import spot.safety.ssbackend.enums.UserRole;

public record RegisterRequest(String username, String password, String schoolName, UserRole role) {
}
