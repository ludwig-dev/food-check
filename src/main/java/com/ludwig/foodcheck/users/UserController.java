package com.ludwig.foodcheck.users;

import com.ludwig.foodcheck.util.EmailValidator;
import com.ludwig.foodcheck.auth.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(Authentication authentication) {
        Long userId = requireAuth(authentication);
        return userService.getUserInfo(userId);
    }

    @PutMapping("/update/username")
    public ResponseEntity<String> updateUsername(@RequestBody Map<String, String> requestBody, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(authentication.getName());
        String newUsername = requestBody.get("newUsername");

        if (newUsername == null || newUsername.trim().isEmpty())
            return new ResponseEntity<>("Username can not be empty", HttpStatus.BAD_REQUEST);

        if (userService.findByUsername(newUsername).isPresent())
            return new ResponseEntity<>("Username already taken", HttpStatus.BAD_REQUEST);

        boolean isUpdated = userService.updateUsername(userId, newUsername);
        if (!isUpdated)
            return new ResponseEntity<>("Failed to update username", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Username updated successfully", HttpStatus.OK);
    }

    @PutMapping("/update/email")
    public ResponseEntity<String> updateEmail(@RequestBody Map<String, String> requestBody, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(authentication.getName());
        String newEmail = requestBody.get("newEmail");

        if (newEmail == null || newEmail.trim().isEmpty())
            return new ResponseEntity<>("Email is empty", HttpStatus.BAD_REQUEST);

        if (!EmailValidator.isValid(newEmail))
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);

        if (userService.findByEmail(newEmail).isPresent())
            return new ResponseEntity<>("Email is taken", HttpStatus.BAD_REQUEST);

        boolean isUpdated = userService.updateEmail(userId, newEmail);
        if (!isUpdated)
            return new ResponseEntity<>("Failed to update username", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Email updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        boolean isUpdated = userService.deleteUserById(userId);
        if (!isUpdated)
            return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Deleted user with id " + userId, HttpStatus.OK);
    }

    private Long requireAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return Long.parseLong(auth.getName());
    }
}

