package spot.safety.ssbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.exception.AccessDeniedException;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolService;
import spot.safety.ssbackend.user.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;
    private final SchoolService schoolService;

    public String login(String username, String password) {
        User user = userService.findUserByName(username);

        if (!passwordEncoder.matches(password, user.getPwdHash())) {
            throw new AccessDeniedException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);

        AuthToken authToken = new AuthToken();
        authToken.setUser(user);
        authToken.setToken(token);
        authToken.setExpirationDate(LocalDateTime.now().plusDays(7));
        authTokenRepository.save(authToken);

        return token;
    }

    public void logout(String token) {
        AuthToken authToken = authTokenRepository.findByToken(token).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
        authTokenRepository.delete(authToken);
    }

    public boolean register(
            String username,
            String password,
            String schoolName,
            UserRole role
    ) {
        User user;
        School school = schoolService.getSchoolByName(schoolName);
        if(role == UserRole.STUDENT) {
            user = new Student(username, password, school, role);
        } else {
            user = new Teacher(username, password, school, role);
        }

        userService.newUser(user);

        return true;
    }
}