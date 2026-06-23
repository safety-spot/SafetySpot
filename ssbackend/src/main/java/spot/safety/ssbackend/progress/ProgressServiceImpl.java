package spot.safety.ssbackend.progress;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.dto.progress.ProgressEntryResponse;
import spot.safety.ssbackend.dto.progress.ProgressSummaryResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.user.UserPrincipal;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {

    private final ImageTagRepository imageTagRepository;

    @Override
    public List<ProgressEntryResponse> getHistory(UserPrincipal actor) {
        assertStudent(actor);

        return imageTagRepository.findAllByStudentId(actor.id()).stream()
                .map(tag -> new ProgressEntryResponse(
                        tag.getImage().getId(),
                        tag.getImage().getTitle(),
                        tag.getImage().getCategory(),
                        tag.getTag(),
                        tag.isCorrect(),
                        tag.getTaggedAt()
                ))
                .sorted(Comparator.comparing(ProgressEntryResponse::taggedAt).reversed())
                .toList();
    }

    @Override
    public ProgressSummaryResponse getSummary(UserPrincipal actor) {
        assertStudent(actor);

        long total = imageTagRepository.countByStudentId(actor.id());
        long correct = imageTagRepository.countByStudentIdAndCorrectTrue(actor.id());
        double accuracy = total == 0 ? 0.0 : (double) correct / total * 100.0;
        return new ProgressSummaryResponse(total, correct, accuracy);
    }

    private void assertStudent(UserPrincipal actor) {
        if (actor.role() != Role.STUDENT) {
            throw new AccessDeniedException("Only STUDENT can view progress");
        }
    }
}
