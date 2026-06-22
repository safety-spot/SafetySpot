package spot.safety.ssbackend.user;

import spot.safety.ssbackend.dto.user.CreateUserRequest;
import spot.safety.ssbackend.dto.user.ResetPasswordRequest;
import spot.safety.ssbackend.dto.user.UpdateUserRequest;
import spot.safety.ssbackend.dto.user.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request, UserPrincipal actor);
    UserResponse getUserById(Long id, UserPrincipal actor);
    List<UserResponse> getUsers(Long classId, UserPrincipal actor);
    UserResponse updateUser(Long id, UpdateUserRequest request, UserPrincipal actor);
    void deactivateUser(Long id, UserPrincipal actor);
    void resetPassword(Long id, ResetPasswordRequest request, UserPrincipal actor);
    User findByUsername(String username);
}
