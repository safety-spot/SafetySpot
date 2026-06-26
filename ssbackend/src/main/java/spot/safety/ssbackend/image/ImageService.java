package spot.safety.ssbackend.image;

import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.image.ImageResponse;
import spot.safety.ssbackend.dto.image.ImageTagResultResponse;
import spot.safety.ssbackend.dto.image.UpdateImageRequest;
import spot.safety.ssbackend.user.UserPrincipal;

import java.util.List;

public interface ImageService {
    ImageResponse createImage(CreateImageRequest request, UserPrincipal actor);

    ImageResponse getImage(Long id, UserPrincipal actor);

    List<ImageResponse> getImages(String category, UserPrincipal actor);

    ImageResponse updateImage(Long id, UpdateImageRequest request, UserPrincipal actor);

    void deleteImage(Long id, UserPrincipal actor);

    List<ImageTagResultResponse> getImageResults(Long id, UserPrincipal actor);
}
