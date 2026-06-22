package spot.safety.ssbackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.dto.user.CreateUserRequest;
import spot.safety.ssbackend.dto.user.ResetPasswordRequest;
import spot.safety.ssbackend.dto.user.UpdateUserRequest;
import spot.safety.ssbackend.dto.user.UserResponse;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.AccessDeniedException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.exception.UsernameAlreadyTakenException;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.school.SchoolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    @Override
    public UserResponse createUser(CreateUserRequest request, UserPrincipal actor) {
        if (actor.role() == Role.TEACHER && request.role() != Role.STUDENT) {
            throw new AccessDeniedException("Teachers may only create STUDENT accounts");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyTakenException(request.username());
        }

        var school = schoolRepository.findById(actor.schoolId())
                .orElseThrow(() -> new EntityNotFoundException("School not found"));

        SchoolClass schoolClass = null;
        if (request.classId() != null) {
            schoolClass = schoolClassRepository.findById(request.classId())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found: " + request.classId()));
            if (actor.role() == Role.TEACHER) {
                assertTeacherOwnsClass(actor, schoolClass);
            }
        }

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .school(school)
                .schoolClass(schoolClass)
                .active(true)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id, UserPrincipal actor) {
        User target = loadUser(id);
        assertReadAccess(actor, target);
        return UserResponse.from(target);
    }

    @Override
    public List<UserResponse> getUsers(Long classId, UserPrincipal actor) {
        if (actor.role() == Role.ADMIN) {
            List<User> users = classId != null
                    ? userRepository.findAllBySchoolClassId(classId)
                    : userRepository.findAllBySchoolId(actor.schoolId());
            return users.stream().map(UserResponse::from).toList();
        }

        // TEACHER: scoped to own classes
        List<Long> ownClassIds = schoolClassRepository.findAllByTeacherId(actor.id())
                .stream().map(SchoolClass::getId).toList();

        if (classId != null) {
            if (!ownClassIds.contains(classId)) {
                throw new AccessDeniedException("Teacher does not own class " + classId);
            }
            return userRepository.findAllBySchoolClassId(classId)
                    .stream().map(UserResponse::from).toList();
        }

        return ownClassIds.stream()
                .flatMap(cid -> userRepository.findAllBySchoolClassId(cid).stream())
                .map(UserResponse::from)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal actor) {
        User target = loadUser(id);
        assertWriteAccess(actor, target);

        if (request.username() != null) {
            if (!request.username().equals(target.getUsername())
                    && userRepository.existsByUsername(request.username())) {
                throw new UsernameAlreadyTakenException(request.username());
            }
            target.setUsername(request.username());
        }

        if (request.classId() != null) {
            SchoolClass newClass = schoolClassRepository.findById(request.classId())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found: " + request.classId()));
            if (actor.role() == Role.TEACHER) {
                assertTeacherOwnsClass(actor, newClass);
            }
            target.setSchoolClass(newClass);
        }

        if (request.active() != null) {
            if (actor.role() != Role.ADMIN) {
                throw new AccessDeniedException("Only ADMIN can change the active flag");
            }
            target.setActive(request.active());
        }

        return UserResponse.from(userRepository.save(target));
    }

    @Override
    public void deactivateUser(Long id, UserPrincipal actor) {
        User target = loadUser(id);
        assertWriteAccess(actor, target);
        target.setActive(false);
        userRepository.save(target);
    }

    @Override
    public void resetPassword(Long id, ResetPasswordRequest request, UserPrincipal actor) {
        User target = loadUser(id);
        assertWriteAccess(actor, target);
        target.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(target);
    }

    // --- helpers ---

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    private void assertReadAccess(UserPrincipal actor, User target) {
        if (actor.role() == Role.ADMIN) return;
        if (actor.role() == Role.STUDENT) {
            if (!actor.id().equals(target.getId())) {
                throw new AccessDeniedException("Students may only view their own profile");
            }
            return;
        }
        // TEACHER
        assertTeacherCanSeeUser(actor, target);
    }

    private void assertWriteAccess(UserPrincipal actor, User target) {
        if (actor.role() == Role.ADMIN) return;
        assertTeacherCanSeeUser(actor, target);
    }

    private void assertTeacherCanSeeUser(UserPrincipal actor, User target) {
        if (actor.id().equals(target.getId())) return; // own profile
        List<Long> ownClassIds = schoolClassRepository.findAllByTeacherId(actor.id())
                .stream().map(SchoolClass::getId).toList();
        if (target.getSchoolClass() == null || !ownClassIds.contains(target.getSchoolClass().getId())) {
            throw new AccessDeniedException("Teacher does not have access to user " + target.getId());
        }
    }

    private void assertTeacherOwnsClass(UserPrincipal actor, SchoolClass schoolClass) {
        if (schoolClass.getTeacher() == null || !actor.id().equals(schoolClass.getTeacher().getId())) {
            throw new AccessDeniedException("Teacher does not own class " + schoolClass.getId());
        }
    }
}
