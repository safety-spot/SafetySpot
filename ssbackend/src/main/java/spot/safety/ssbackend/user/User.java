package spot.safety.ssbackend.user;

import jakarta.persistence.*;
import lombok.Data;
import spot.safety.ssbackend.auth.AuthToken;
import spot.safety.ssbackend.enums.UserRole;
import spot.safety.ssbackend.school.School;

import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String username;
    private String pwdHash;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "user")
    private List<AuthToken> authTokens;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    public User(String username, String pwdHash, School school, UserRole userRole) {
        this.username = username;
        this.pwdHash = pwdHash;
        this.school = school;
        this.userRole = userRole;
    }

    public User() {
    }

    public boolean login() {
        return false;
    }

    public void logout() {
    }

    public void resetPassword(String newHash) {
        this.pwdHash = newHash;
    }
}
