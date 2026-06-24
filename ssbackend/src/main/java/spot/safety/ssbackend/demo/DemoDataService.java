package spot.safety.ssbackend.demo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserRepository;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class DemoDataService {

    private final UserRepository userRepository;

    public DemoDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void seedDatabase() {
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder().build())
                .toList();

        userRepository.saveAll(users);
    }
}