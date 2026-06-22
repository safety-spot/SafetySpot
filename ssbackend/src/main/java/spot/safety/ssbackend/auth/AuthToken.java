package spot.safety.ssbackend.auth;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import spot.safety.ssbackend.user.User;

import java.time.LocalDateTime;

@Entity
@Data
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @ManyToOne
    private User user;

    private LocalDateTime expirationDate;


}
