package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.dto.request.LoginRequest;
import com.subscript.subscription_billing_system.dto.request.RegisterRequest;
import com.subscript.subscription_billing_system.dto.response.AuthResponse;
import com.subscript.subscription_billing_system.entity.User;
import com.subscript.subscription_billing_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

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