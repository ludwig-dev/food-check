package com.ludwig.foodcheck.controller;

import com.ludwig.foodcheck.dto.UserDTO;
import com.ludwig.foodcheck.service.UserService;
import com.ludwig.foodcheck.util.EmailValidator;
import com.ludwig.foodcheck.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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

    @GetMapping("/get/userinfo")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized - Invalid Token");
        }

        Long userId = Long.parseLong(authentication.getName());
        UserDTO userDTO = userService.getUserInfo(userId);
        if (userDTO == null)
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication) {

        Long userId = Long.parseLong(authentication.getName());

        boolean isUpdated = userService.deleteUserById(userId);
        if (!isUpdated)
            return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Deleted user with id " + userId, HttpStatus.OK);
    }
}

