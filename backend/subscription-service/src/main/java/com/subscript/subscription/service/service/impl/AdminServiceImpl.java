package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.User;
import com.subscript.subscription.api.wrapper.request.EmployeeRequest;
import com.subscript.subscription.api.wrapper.request.ResetPasswordRequest;
import com.subscript.subscription.api.wrapper.request.UpdateUserStatusRequest;
import com.subscript.subscription.api.wrapper.response.UserResponse;
import com.subscript.subscription.service.repository.UserRepository;
import com.subscript.subscription.service.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create Employee
     * Customers are NOT allowed here.
     */
    @Override
    public UserResponse createEmployee(EmployeeRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email already exists: " + request.getEmail());
        }

        if (request.getRole() == User.Role.customer) {
            throw new RuntimeException(
                    "Customers must register using /api/auth/register");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());
        user.setStatus(User.Status.active);

        User savedUser = userRepository.save(user);

        return mapToResponse(
                savedUser,
                "Employee created successfully.");
    }

    /**
     * Return ALL users.
     * Admin can view both customers and employees.
     */
    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> mapToResponse(user, null))
                .collect(Collectors.toList());
    }

    /**
     * Return any user by id.
     */
    @Override
    public UserResponse getUserById(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + userId));

        return mapToResponse(user, null);
    }

    /**
     * Activate / Deactivate any user.
     */
    @Override
    public UserResponse updateUserStatus(
            Integer userId,
            UpdateUserStatusRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + userId));

        user.setStatus(request.getStatus());

        User updatedUser = userRepository.save(user);

        return mapToResponse(
                updatedUser,
                "User status updated successfully.");
    }

    /**
     * Reset password for any user.
     */
    @Override
    public void resetPassword(
            Integer userId,
            ResetPasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + userId));

        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    /**
     * Common mapper.
     */
    private UserResponse mapToResponse(
            User user,
            String message) {

        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name(),
                user.getStatus().name(),
                message);
    }
}