package spot.safety.ssbackend.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.dto.stats.ClassStatsResponse;
import spot.safety.ssbackend.dto.stats.ImageStatsResponse;
import spot.safety.ssbackend.dto.stats.StudentStatEntry;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.AccessDeniedException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.model.Image;
import spot.safety.ssbackend.model.ImageTag;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.repository.ImageRepository;
import spot.safety.ssbackend.repository.ImageTagRepository;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageTagRepository imageTagRepository;

    @Override
    public ClassStatsResponse getClassStats(Long classId, UserPrincipal actor) {
        SchoolClass schoolClass = loadAccessibleClass(classId, actor);

        List<User> students = userRepository.findAllBySchoolClassId(classId).stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .sorted(Comparator.comparing(User::getUsername))
                .toList();

        Map<Long, List<ImageTag>> tagsByStudent = imageTagRepository.findAllByStudentClassId(classId).stream()
                .collect(Collectors.groupingBy(tag -> tag.getStudent().getId()));

        List<StudentStatEntry> entries = students.stream()
                .map(student -> toStudentStat(student, tagsByStudent.getOrDefault(student.getId(), List.of())))
                .toList();

        return new ClassStatsResponse(
                schoolClass.getId(),
                schoolClass.getName(),
                (int) imageRepository.countByActiveTrue(),
                students.size(),
                entries
        );
    }

    @Override
    public ImageStatsResponse getImageStats(Long imageId, UserPrincipal actor) {
        assertTeacherOrAdmin(actor);

        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + imageId));

        List<ImageTag> tags = imageTagRepository.findAllByImageId(imageId);
        long totalResponses = tags.size();
        long correctResponses = tags.stream().filter(ImageTag::isCorrect).count();
        int dangerousCount = (int) tags.stream().filter(tag -> tag.getTag() == TagValue.DANGEROUS).count();
        int safeCount = (int) tags.stream().filter(tag -> tag.getTag() == TagValue.SAFE).count();
        double correctRate = totalResponses == 0 ? 0.0 : (double) correctResponses / totalResponses;

        return new ImageStatsResponse(
                image.getId(),
                image.getTitle(),
                image.getCorrectTag(),
                (int) totalResponses,
                (int) correctResponses,
                correctRate,
                dangerousCount,
                safeCount
        );
    }

    private StudentStatEntry toStudentStat(User student, List<ImageTag> tags) {
        long totalTagged = tags.size();
        long correctCount = tags.stream().filter(ImageTag::isCorrect).count();
        double accuracy = totalTagged == 0 ? 0.0 : (double) correctCount / totalTagged * 100.0;
        return new StudentStatEntry(student.getId(), student.getUsername(), totalTagged, correctCount, accuracy);
    }

    private SchoolClass loadAccessibleClass(Long classId, UserPrincipal actor) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class not found: " + classId));

        if (actor.role() == Role.TEACHER) {
            if (schoolClass.getTeacher() == null || !actor.id().equals(schoolClass.getTeacher().getId())) {
                throw new AccessDeniedException("Teacher does not own class " + classId);
            }
            return schoolClass;
        }

        if (actor.role() == Role.ADMIN) {
            if (schoolClass.getSchool() == null || !actor.schoolId().equals(schoolClass.getSchool().getId())) {
                throw new AccessDeniedException("Admin cannot access class " + classId);
            }
            return schoolClass;
        }

        throw new AccessDeniedException("Not authorized");
    }

    private void assertTeacherOrAdmin(UserPrincipal actor) {
        if (actor.role() != Role.TEACHER && actor.role() != Role.ADMIN) {
            throw new AccessDeniedException("Not authorized");
        }
    }
}
