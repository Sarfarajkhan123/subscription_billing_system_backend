package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.AuthService;

import com.subscript.subscription.api.wrapper.request.LoginRequest;
import com.subscript.subscription.api.wrapper.request.RegisterRequest;
import com.subscript.subscription.api.wrapper.response.AuthResponse;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    // Register user
    public AuthResponse register(RegisterRequest request) {

        System.out.println("========== REGISTER API CALLED ==========");

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            System.out.println("Email already exists!");
            throw new RuntimeException("Email already exists");
        }

        // Create User object
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        // Temporary (we'll encrypt later)
        user.setPasswordHash(request.getPassword());

        user.setPhone(request.getPhone());

        // Default values
        user.setRole(User.Role.customer);
        user.setStatus(User.Status.active);

        System.out.println("Saving user to database...");

        User savedUser = userRepository.save(user);

        System.out.println("User saved successfully!");
        System.out.println("User ID: " + savedUser.getUserId());
        System.out.println("Email: " + savedUser.getEmail());

        return new AuthResponse(
                "Registration Successful",
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getRole().name());
    }

    // Login user
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Temporary password check
        if (!user.getPasswordHash().equals(request.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Login successful
        return new AuthResponse(
                "Login Successful",
                user.getEmail(),
                user.getFirstName(),
                user.getRole().name());
    }
}