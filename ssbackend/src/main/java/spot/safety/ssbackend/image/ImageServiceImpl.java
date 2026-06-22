package spot.safety.ssbackend.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.image.ImageResponse;
import spot.safety.ssbackend.dto.image.ImageTagResultResponse;
import spot.safety.ssbackend.dto.image.UpdateImageRequest;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.AccessDeniedException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageTagRepository imageTagRepository;
    private final UserRepository userRepository;

    @Override
    public ImageResponse createImage(CreateImageRequest request, UserPrincipal actor) {
        assertTeacherOrAdmin(actor);
        User uploadedBy = loadUser(actor.id());

        Image image = Image.builder()
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .category(request.category())
                .correctTag(request.correctTag())
                .feedbackCorrect(request.feedbackCorrect())
                .feedbackWrong(request.feedbackWrong())
                .uploadedBy(uploadedBy)
                .active(true)
                .build();

        return toResponse(imageRepository.save(image), actor);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageResponse getImage(Long id, UserPrincipal actor) {
        return toResponse(loadActiveImage(id), actor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageResponse> getImages(String category, UserPrincipal actor) {
        List<Image> images = category == null
                ? imageRepository.findAllByActiveTrue()
                : imageRepository.findAllByActiveTrueAndCategory(category);
        return images.stream().map(image -> toResponse(image, actor)).toList();
    }

    @Override
    public ImageResponse updateImage(Long id, UpdateImageRequest request, UserPrincipal actor) {
        Image image = loadAnyImage(id);
        assertCanEdit(image, actor);

        if (request.title() != null) {
            image.setTitle(request.title());
        }
        if (request.description() != null) {
            image.setDescription(request.description());
        }
        if (request.imageUrl() != null) {
            image.setImageUrl(request.imageUrl());
        }
        if (request.category() != null) {
            image.setCategory(request.category());
        }
        if (request.correctTag() != null) {
            image.setCorrectTag(request.correctTag());
        }
        if (request.feedbackCorrect() != null) {
            image.setFeedbackCorrect(request.feedbackCorrect());
        }
        if (request.feedbackWrong() != null) {
            image.setFeedbackWrong(request.feedbackWrong());
        }

        return toResponse(imageRepository.save(image), actor);
    }

    @Override
    public void deleteImage(Long id, UserPrincipal actor) {
        Image image = loadAnyImage(id);
        assertCanEdit(image, actor);
        image.setActive(false);
        imageRepository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageTagResultResponse> getImageResults(Long id, UserPrincipal actor) {
        assertTeacherOrAdmin(actor);
        loadAnyImage(id);

        return imageTagRepository.findAllByImageId(id).stream()
                .map(this::toResultResponse)
                .toList();
    }

    private ImageResponse toResponse(Image image, UserPrincipal actor) {
        TagValue correctTag = actor.role() == Role.STUDENT ? null : image.getCorrectTag();
        return new ImageResponse(
                image.getId(),
                image.getTitle(),
                image.getDescription(),
                image.getImageUrl(),
                image.getCategory(),
                correctTag,
                image.getUploadedBy().getId(),
                image.getUploadedBy().getUsername(),
                image.getCreatedAt(),
                image.getUpdatedAt()
        );
    }

    private ImageTagResultResponse toResultResponse(ImageTag tag) {
        return new ImageTagResultResponse(
                tag.getStudent().getId(),
                tag.getStudent().getUsername(),
                tag.getTag(),
                tag.isCorrect(),
                tag.getTaggedAt()
        );
    }

    private Image loadActiveImage(Long id) {
        Image image = loadAnyImage(id);
        if (!image.isActive()) {
            throw new EntityNotFoundException("Image not found: " + id);
        }
        return image;
    }

    private Image loadAnyImage(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + id));
    }

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private void assertTeacherOrAdmin(UserPrincipal actor) {
        if (actor.role() != Role.TEACHER && actor.role() != Role.ADMIN) {
            throw new AccessDeniedException("Not authorized");
        }
    }

    private void assertCanEdit(Image image, UserPrincipal actor) {
        boolean isOwner = image.getUploadedBy().getId().equals(actor.id());
        boolean isAdmin = actor.role() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Not image owner");
        }
    }
}
