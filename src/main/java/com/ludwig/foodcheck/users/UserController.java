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

    @PatchMapping("/me")
    public UserDTO patchCurrentUser(@RequestBody UserUpdateDTO upd,
                                    Authentication authentication) {
        Long userId = requireAuth(authentication);
        return userService.updateUser(userId, upd);
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

