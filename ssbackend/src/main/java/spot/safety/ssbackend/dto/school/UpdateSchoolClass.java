package spot.safety.ssbackend.dto.school;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public record UpdateSchoolClass(
        @Nullable @Size(max = 50) String name,
        @Nullable Long teacherId
) {

}
