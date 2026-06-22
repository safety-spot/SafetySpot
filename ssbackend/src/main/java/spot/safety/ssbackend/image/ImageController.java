package spot.safety.ssbackend.image;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.image.ImageResponse;
import spot.safety.ssbackend.dto.image.ImageTagResultResponse;
import spot.safety.ssbackend.dto.image.UpdateImageRequest;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping
    public ResponseEntity<ImageResponse> createImage(
            @RequestBody @Valid CreateImageRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.createImage(request, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('STUDENT','TEACHER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<ImageResponse>> getImages(
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(imageService.getImages(category, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ImageResponse> getImage(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(imageService.getImage(id, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ImageResponse> updateImage(
            @PathVariable Long id,
            @RequestBody @Valid UpdateImageRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(imageService.updateImage(id, request, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        imageService.deleteImage(id, UserPrincipal.from(principal.getUser()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping("/{id}/results")
    public ResponseEntity<List<ImageTagResultResponse>> getImageResults(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(imageService.getImageResults(id, UserPrincipal.from(principal.getUser())));
    }
}
