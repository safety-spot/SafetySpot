package spot.safety.ssbackend.dto;

import java.time.Instant;

public record ErrorResponse(int errorCode, String message, Instant timestamp) {
}
