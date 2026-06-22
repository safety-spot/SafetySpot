package spot.safety.ssbackend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.safety.ssbackend.dto.user.CreateUserRequest;
import spot.safety.ssbackend.dto.user.ResetPasswordRequest;
import spot.safety.ssbackend.dto.user.UpdateUserRequest;
import spot.safety.ssbackend.dto.user.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) Long classId,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(userService.getUsers(classId, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid CreateUserRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        UserResponse response = userService.createUser(request, UserPrincipal.from(principal.getUser()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('STUDENT','TEACHER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(userService.getUserById(id, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        return ResponseEntity.ok(userService.updateUser(id, request, UserPrincipal.from(principal.getUser())));
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser principal) {
        userService.deactivateUser(id, UserPrincipal.from(principal.getUser()));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('TEACHER','ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody @Valid ResetPasswordRequest request,
            @AuthenticationPrincipal SecurityUser principal) {
        userService.resetPassword(id, request, UserPrincipal.from(principal.getUser()));
        return ResponseEntity.noContent().build();
    }
}
