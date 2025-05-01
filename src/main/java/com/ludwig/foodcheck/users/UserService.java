package com.ludwig.foodcheck.users;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username.toLowerCase());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email.toLowerCase());
    }

    public void registerNewUser(User user) {
        user.setRole("USER");
        user.setEmail(user.getEmail().toLowerCase());
        user.setUsername(user.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public boolean updateUsername(Long userId, String newUsername) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return false;

        User user = userOptional.get();
        user.setUsername(newUsername.toLowerCase());
        userRepository.save(user);
        return true;
    }

    public boolean updateEmail(Long userId, String newEmail) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return false;

        User user = userOptional.get();
        user.setEmail(newEmail.toLowerCase());
        userRepository.save(user);
        return true;
    }

    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean setUserRole(Long userId, String role) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return false;
        User user = userOptional.get();
        user.setRole(role);
        userRepository.save(user);
        return true;
    }

    public boolean deleteUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return false;
        User user = userOptional.get();
        userRepository.delete(user);
        return true;
    }

    public List<UserDTO> searchByUsername(String username) {
        List<User> userList = userRepository.findByUsernameContainingIgnoreCase(username);
        return userList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

}
