package spot.safety.ssbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.exception.EntityNotFoundException;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolService;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenRepository authTokenRepository;
    private final SchoolService schoolService;

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new spot.safety.ssbackend.exception.EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
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
        AuthToken authToken = authTokenRepository.findByToken(token).getFirst();
        authTokenRepository.delete(authToken);
    }

    public boolean register(String username, String password, String schoolName, Role role) {
        // TODO: Implement proper school initialization. See https://github.com/safety-spot/SafetySpot/issues/57.
        School school;
        try {
            school = schoolService.getSchoolByName(schoolName);
        }
        catch (EntityNotFoundException e) {
            school = School.builder().name(schoolName).build();
            schoolService.newSchool(school);
        }
        User user = User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .school(school)
                .active(true)
                .build();
        userRepository.save(user);
        return true;
    }
}
