package spot.safety.ssbackend.tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spot.safety.ssbackend.dto.tag.SubmitTagRequest;
import spot.safety.ssbackend.dto.tag.TagResponse;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{imageId}/tag")
    public ResponseEntity<TagResponse> submitTag(
            @PathVariable Long imageId,
            @RequestBody @Valid SubmitTagRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.submitTag(imageId, request, UserPrincipal.from(principal.getUser())));
    }
}
