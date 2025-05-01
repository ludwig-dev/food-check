package com.ludwig.foodcheck.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/set-admin")
    public ResponseEntity<String> setRoleToAdmin(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("id");
        boolean isUpdated = userService.setUserRole(userId, "ADMIN");
        if (!isUpdated)
            return new ResponseEntity<>("Failed to set role to admin", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Changed role to ADMIN", HttpStatus.OK);
    }

    @PutMapping("/set-user")
    public ResponseEntity<String> setRoleToUser(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("id");
        boolean isUpdated = userService.setUserRole(userId, "USER");
        if (!isUpdated)
            return new ResponseEntity<>("Failed to set role to user", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Changed role to user", HttpStatus.OK);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("id");
        userService.deleteUserById(userId);
//        if (!isUpdated)
//            return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Deleted user with id: " + userId, HttpStatus.OK);
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<UserDTO>> searchByUsername(@RequestParam("username") String username) {
        List<UserDTO> users = userService.searchByUsername(username);
        return ResponseEntity.ok(users);
    }

}

