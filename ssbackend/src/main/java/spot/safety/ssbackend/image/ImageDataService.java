package spot.safety.ssbackend.image;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import spot.safety.ssbackend.user.UserPrincipal;

public interface ImageDataService {
    /**
     * Store binary data for an existing Image identified by id. Will replace any existing data.
     * Also updates the associated Image.imageUrl to point to the data endpoint.
     */
    void store(Long id, MultipartFile file, UserPrincipal actor);

    /**
     * Load image data as a Spring Resource for serving.
     */
    Resource loadAsResource(Long id);

    /**
     * Store binary data (bytes) for an existing Image. Filename is provided for reference
     * (not required for storage) and can be used by callers that want to preserve extension.
     */
    void storeBytes(Long id, byte[] data, String filename, UserPrincipal actor);
}

