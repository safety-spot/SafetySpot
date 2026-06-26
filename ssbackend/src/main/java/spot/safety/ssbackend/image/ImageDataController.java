package spot.safety.ssbackend.image;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spot.safety.ssbackend.user.SecurityUser;
import spot.safety.ssbackend.user.UserPrincipal;


@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageDataController {

    private final ImageDataService imageDataService;

    @PreAuthorize("hasAnyAuthority('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}/data")
    public ResponseEntity<Resource> getImageData(@PathVariable Long id) {

        Resource resource = imageDataService.loadAsResource(id);
        String contentType = null;
        try {
            contentType = java.nio.file.Files.probeContentType(resource.getFile().toPath());
        } catch (Exception ignored) {
        }

        MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + "\"")
                .contentType(mediaType)
                .body(resource);
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping(value = "/{id}/data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImageData(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal SecurityUser principal) {

        imageDataService.store(id, file, UserPrincipal.from(principal.getUser()));
        return ResponseEntity.noContent().build();
    }
}



