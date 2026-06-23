package spot.safety.ssbackend.dto.school;

import jakarta.annotation.Nullable;

public record UpdateSchoolClass(
        @Nullable String name,
        @Nullable Long teacherId
) {

}
