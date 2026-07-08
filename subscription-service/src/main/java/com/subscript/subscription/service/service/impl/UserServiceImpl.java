package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.UserService;

import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // CREATE — save a new user
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    // READ ALL — get every user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // READ ONE — get user by ID
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    // READ ONE — get user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    // UPDATE — update an existing user
    public User updateUser(Integer userId, User updatedData) {
        // First find the existing user
        User existingUser = getUserById(userId);

        // Only update fields that are provided
        if (updatedData.getFirstName() != null) {
            existingUser.setFirstName(updatedData.getFirstName());
        }
        if (updatedData.getLastName() != null) {
            existingUser.setLastName(updatedData.getLastName());
        }
        if (updatedData.getPhone() != null) {
            existingUser.setPhone(updatedData.getPhone());
        }
        if (updatedData.getRole() != null) {
            existingUser.setRole(updatedData.getRole());
        }
        if (updatedData.getStatus() != null) {
            existingUser.setStatus(updatedData.getStatus());
        }

        // Save updated user back to database
        return userRepository.save(existingUser);
    }

    // DELETE — permanently delete (only use for testing; in real app use
    // deactivate)
    public void deleteUser(Integer userId) {
        // Check if user exists first
        getUserById(userId); // throws if not found
        userRepository.deleteById(userId);
    }

    // DEACTIVATE — set status to inactive (safer than delete)
    public User deactivateUser(Integer userId) {
        User user = getUserById(userId);
        user.setStatus(User.Status.inactive);
        return userRepository.save(user);
    }

    // ACTIVATE — set status back to active
    public User activateUser(Integer userId) {
        User user = getUserById(userId);
        user.setStatus(User.Status.active);
        return userRepository.save(user);
    }
}