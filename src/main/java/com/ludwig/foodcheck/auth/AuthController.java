package com.ludwig.foodcheck.auth;

import com.ludwig.foodcheck.users.User;
import com.ludwig.foodcheck.users.UserService;
import com.ludwig.foodcheck.util.EmailValidator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        if (!EmailValidator.isValid(user.getEmail())) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        if (userService.findByUsername(user.getUsername()).isPresent())
            return new ResponseEntity<>("Username already exists", HttpStatus.BAD_REQUEST);

        if (userService.findByEmail(user.getEmail()).isPresent())
            return new ResponseEntity<>("Email already exits", HttpStatus.BAD_REQUEST);

        userService.registerNewUser(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletResponse response) {

        if (!EmailValidator.isValid(user.getEmail())) {
            return new ResponseEntity<>("Invalid email format", HttpStatus.BAD_REQUEST);
        }

        Optional<User> foundUser = userService.findByEmail(user.getEmail());

        if (foundUser.isEmpty()) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), foundUser.get().getPassword())) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.NOT_FOUND);
        }

        // Generate JWT Token
        String token = jwtUtil.generateToken(foundUser.get());

        // Create HTTP-Only Secure Cookie
        ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                .httpOnly(true)       // Prevents XSS attacks
                .secure(true)         // end only over HTTPS
                .sameSite("Strict")   // Prevents CSRF attacks
                .path("/")            // Available for the whole application
                .maxAge(Duration.ofDays(7)) // Expires after 7 days
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletResponse response) {
        // Create an empty cookie with maxAge = 0 to clear it
        ResponseCookie expiredCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // Expires immediately
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());

        return ResponseEntity.ok().build();
    }
}
