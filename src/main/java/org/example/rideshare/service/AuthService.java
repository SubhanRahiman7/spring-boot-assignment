package org.example.rideshare.service;

import org.example.rideshare.dto.AuthResponse;
import org.example.rideshare.dto.LoginRequest;
import org.example.rideshare.dto.RegisterRequest;
import org.example.rideshare.exception.BadRequestException;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.UserRepository;
import org.example.rideshare.util.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Set<String> VALID_ROLES = Set.of("ROLE_USER", "ROLE_DRIVER");

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        validateUsernameAvailability(request.getUsername());
        validateRole(request.getRole());

        User newUser = buildUserFromRequest(request);
        User savedUser = userRepository.save(newUser);

        String jwtToken = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getRole());
        return buildAuthResponse(jwtToken, savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        User user = findUserByUsername(request.getUsername());
        validatePassword(request.getPassword(), user.getPassword());

        String jwtToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return buildAuthResponse(jwtToken, user);
    }

    private void validateUsernameAvailability(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already exists");
        }
    }

    private void validateRole(String role) {
        if (!VALID_ROLES.contains(role)) {
            throw new BadRequestException("Role must be ROLE_USER or ROLE_DRIVER");
        }
    }

    private User buildUserFromRequest(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return user;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}

