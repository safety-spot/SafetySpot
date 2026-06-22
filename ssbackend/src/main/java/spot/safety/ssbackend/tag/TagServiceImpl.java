package spot.safety.ssbackend.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.dto.tag.SubmitTagRequest;
import spot.safety.ssbackend.dto.tag.TagResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.AccessDeniedException;
import spot.safety.ssbackend.exception.DuplicateTagException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final ImageRepository imageRepository;
    private final ImageTagRepository imageTagRepository;
    private final UserRepository userRepository;

    @Override
    public TagResponse submitTag(Long imageId, SubmitTagRequest request, UserPrincipal actor) {
        assertStudent(actor);

        Image image = imageRepository.findById(imageId)
                .filter(Image::isActive)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + imageId));

        if (imageTagRepository.existsByImageIdAndStudentId(imageId, actor.id())) {
            throw new DuplicateTagException("Image already tagged");
        }

        User student = userRepository.findById(actor.id())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + actor.id()));

        boolean correct = request.tag() == image.getCorrectTag();
        String feedback = correct ? image.getFeedbackCorrect() : image.getFeedbackWrong();

        ImageTag tag = imageTagRepository.save(ImageTag.builder()
                .image(image)
                .student(student)
                .tag(request.tag())
                .correct(correct)
                .build());

        return new TagResponse(imageId, request.tag(), correct, feedback, tag.getTaggedAt());
    }

    private void assertStudent(UserPrincipal actor) {
        if (actor.role() != Role.STUDENT) {
            throw new AccessDeniedException("Only STUDENT can tag images");
        }
    }
}
