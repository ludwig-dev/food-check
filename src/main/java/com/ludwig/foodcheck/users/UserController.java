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

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(Authentication auth) {
        Long userId = requireAuth(auth);
        userService.deleteUserById(userId);
    }

    private Long requireAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return Long.parseLong(auth.getName());
    }
}

