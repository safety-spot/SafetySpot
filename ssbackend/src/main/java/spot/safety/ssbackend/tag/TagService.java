package spot.safety.ssbackend.tag;

import spot.safety.ssbackend.dto.tag.SubmitTagRequest;
import spot.safety.ssbackend.dto.tag.TagResponse;
import spot.safety.ssbackend.user.UserPrincipal;

public interface TagService {
    TagResponse submitTag(Long imageId, SubmitTagRequest request, UserPrincipal actor);
}
