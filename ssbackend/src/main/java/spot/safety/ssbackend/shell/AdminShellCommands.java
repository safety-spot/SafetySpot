package spot.safety.ssbackend.shell;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import spot.safety.ssbackend.demo.DemoDataService;
import spot.safety.ssbackend.dto.user.ResetPasswordRequest;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolRepository;
import spot.safety.ssbackend.school.SchoolService;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;
import spot.safety.ssbackend.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@Profile("!prod")
@AllArgsConstructor
public class AdminShellCommands {
    private final DemoDataService demoDataService;
    private final SchoolRepository schoolRepository;
    private final SchoolService schoolService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Command(name = "seed-data", description = "Fills the database with demo data.")
    public String seedData() {
        try {
            demoDataService.seedDatabase();
            return "Success: Successfully planted demo data in the database!";
        } catch (Exception e) {
            StringBuilder s = new StringBuilder();
            for (StackTraceElement traceElement : e.getStackTrace())
                s.append("\tat ").append(traceElement).append("\n");

            return s + "Error: Failed to seed data: " + e.getMessage();
        }
    }

    @Command(name = "create-school", description = "Creates a new school and prints its id")
    public String createSchool(@Option(longName = "name", description = "School name") String name) {
        try {
            if (name == null || name.isBlank()) {
                return "Error: --name is required";
            }
            if (schoolRepository.existsByName(name)) {
                return "Error: School with name '" + name + "' already exists";
            }
            School school = School.builder()
                    .name(name.trim())
                    .build();
            // Ensure defaults are applied by builder/Entity
            School saved = schoolRepository.save(school);
            return "Success: Created school with id " + saved.getId();
        } catch (Exception e) {
            return formatException(e, "Failed to create school");
        }
    }

    @Command(name = "create-admin", description = "Creates an ADMIN user with hashed password")
    public String createAdmin(@Option(longName = "username", description = "Username") String username,
                              @Option(longName = "password", description = "Password") String password,
                              @Option(longName = "school-id", description = "School id") Long schoolId) {
        try {
            if (username == null || username.isBlank()) return "Error: --username is required";
            if (password == null || password.isBlank()) return "Error: --password is required";
            if (schoolId == null) return "Error: --school-id is required";

            if (userRepository.existsByUsername(username)) {
                return "Error: Username '" + username + "' already taken";
            }

            var school = schoolService.getSchoolById(schoolId);

            User user = User.builder()
                    .username(username.trim())
                    .passwordHash(passwordEncoder.encode(password))
                    .role(spot.safety.ssbackend.enums.Role.ADMIN)
                    .school(school)
                    .active(true)
                    .build();
            User saved = userRepository.saveAndFlush(user);
            return "Success: Created ADMIN user with id " + saved.getId();
        } catch (Exception e) {
            return formatException(e, "Failed to create admin");
        }
    }

    @Command(name = "activate-license", description = "Activates a school's license with given expiry (YYYY-MM-DD)")
    public String activateLicense(@Option(longName = "school-id", description = "School id") Long schoolId,
                                  @Option(longName = "expiry", description = "Expiry date YYYY-MM-DD") String expiry) {
        try {
            if (schoolId == null) return "Error: --school-id is required";
            if (expiry == null || expiry.isBlank()) return "Error: --expiry is required";

            LocalDate expiryDate;
            try {
                expiryDate = LocalDate.parse(expiry);
            } catch (Exception ex) {
                return "Error: --expiry must be in format YYYY-MM-DD";
            }

            if (expiryDate.isBefore(LocalDate.now())) {
                return "Error: expiry date is in the past";
            }

            var school = schoolService.getSchoolById(schoolId);
            school.setLicenseExpiry(expiryDate);
            school.setLicenseStatus(spot.safety.ssbackend.enums.LicenseStatus.ACTIVE);
            school.setLicenseKey(UUID.randomUUID().toString());
            schoolRepository.save(school);
            return "Success: Activated license for school id " + school.getId() + " until " + expiryDate;
        } catch (Exception e) {
            return formatException(e, "Failed to activate license");
        }
    }

    @Command(name = "list-schools", description = "Lists all schools with license status")
    public String listSchools() {
        try {
            List<School> schools = schoolRepository.findAll();
            if (schools.isEmpty()) return "No schools found";

            StringBuilder out = new StringBuilder();
            out.append(String.format("%-5s | %-30s | %-10s | %-10s\n", "ID", "NAME", "STATUS", "EXPIRY"));
            out.append("--------------------------------------------------------------------------------\n");
            for (School s : schools) {
                out.append(String.format("%-5s | %-30s | %-10s | %-10s\n",
                        s.getId(), s.getName(), s.getLicenseStatus(), s.getLicenseExpiry()));
            }
            return out.toString();
        } catch (Exception e) {
            return formatException(e, "Failed to list schools");
        }
    }

    @Command(name = "reset-password", description = "Force-resets a user's password")
    public String resetPassword(@Option(longName = "user-id", description = "User id") Long userId,
                                @Option(longName = "password", description = "New password") String password) {
        try {
            if (userId == null) return "Error: --user-id is required";
            if (password == null || password.isBlank()) return "Error: --password is required";

            // run as a synthetic ADMIN principal
            UserPrincipal adminPrincipal = new UserPrincipal(0L, "shell", spot.safety.ssbackend.enums.Role.ADMIN, 0L, null);
            userService.resetPassword(userId, new ResetPasswordRequest(password), adminPrincipal);
            return "Success: Password reset for user id " + userId;
        } catch (Exception e) {
            return formatException(e, "Failed to reset password");
        }
    }

    // --- helpers ---
    private String formatException(Exception e, String prefix) {
        StringBuilder s = new StringBuilder();
        for (StackTraceElement traceElement : e.getStackTrace())
            s.append("\tat ").append(traceElement).append("\n");
        return s + "Error: " + prefix + ": " + e.getMessage();
    }
}