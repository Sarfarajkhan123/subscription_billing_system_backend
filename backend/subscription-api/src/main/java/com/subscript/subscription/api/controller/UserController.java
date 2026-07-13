package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    ResponseEntity<User> createUser(User user);

    ResponseEntity<List<User>> getAllUsers();

    ResponseEntity<User> getUserById(Integer id);

    ResponseEntity<User> getUserByEmail(String email);

    ResponseEntity<User> updateUser(
            Integer id,
            User updatedData);

    ResponseEntity<User> deactivateUser(Integer id);

    ResponseEntity<User> activateUser(Integer id);

    ResponseEntity<String> deleteUser(Integer id);
}