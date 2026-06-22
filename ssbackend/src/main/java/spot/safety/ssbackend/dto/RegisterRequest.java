package spot.safety.ssbackend.dto;

import spot.safety.ssbackend.enums.Role;

public record RegisterRequest(String username, String password, String schoolName, Role role) {
}
