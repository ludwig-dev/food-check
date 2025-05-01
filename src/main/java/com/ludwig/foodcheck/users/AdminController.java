package com.ludwig.foodcheck.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> listUsers(@RequestParam(value = "username", required = false) String username) {
        return (username != null)
                ? userService.searchByUsername(username)
                : userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDTO updateRole(@PathVariable Long id,
                              @RequestBody UserRoleUpdateDTO dto) {
        return userService.updateUserRole(id, dto.getRole());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}

