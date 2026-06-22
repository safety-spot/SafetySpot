package spot.safety.ssbackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public User findUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public void newUser(User user) {
        if (user.getUserRole() == UserRole.STUDENT) {
            Student student = new Student(user.getUsername(), user.getPwdHash(), user.getSchool(), user.getUserRole());
            studentRepository.save(student);
        } else {
            Teacher teacher = new Teacher(user.getUsername(), user.getPwdHash(), user.getSchool(), user.getUserRole());
            teacherRepository.save(teacher);
        }
    }
}
