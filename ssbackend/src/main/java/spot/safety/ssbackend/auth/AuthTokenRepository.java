package spot.safety.ssbackend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    List<AuthToken> findByToken(String token);
}
