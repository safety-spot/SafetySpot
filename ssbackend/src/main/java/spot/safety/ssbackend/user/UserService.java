package spot.safety.ssbackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;


    public User findUserByName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public void newUser(User user) {
        switch (user.getUserRole()) {
            case STUDENT -> user = new Student(
                    user.getUsername(),
                    user.getPwdHash(),
                    user.getSchool(),
                    user.getUserRole()
            );
            case TEACHER -> user = new Teacher(
                    user.getUsername(),
                    user.getPwdHash(),
                    user.getSchool(),
                    user.getUserRole()
            );
        }

        if(user.getUserRole() == UserRole.STUDENT) {
            assert user instanceof Student;
            studentRepository.save((Student) user);
        } else {
            assert user instanceof Teacher;
            teacherRepository.save((Teacher) user);
        }
    }

}
