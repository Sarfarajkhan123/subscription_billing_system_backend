package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.User;
import com.subscript.subscription_billing_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    // ── CREATE ──────────────────────────────────────────────
    // POST http://localhost:8080/api/users
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    // ── READ ALL ─────────────────────────────────────────────
    // GET http://localhost:8080/api/users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ── READ ONE BY ID ────────────────────────────────────────
    // GET http://localhost:8080/api/users/1
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ── READ ONE BY EMAIL ─────────────────────────────────────
    // GET http://localhost:8080/api/users/email/john@company.com
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // ── UPDATE ────────────────────────────────────────────────
    // PUT http://localhost:8080/api/users/1
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedData) {
        return ResponseEntity.ok(userService.updateUser(id, updatedData));
    }

    // ── DEACTIVATE (soft delete) ──────────────────────────────
    // PUT http://localhost:8080/api/users/1/deactivate
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    // ── ACTIVATE ─────────────────────────────────────────────
    // PUT http://localhost:8080/api/users/1/activate
    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    // ── DELETE (hard delete - for testing only) ───────────────
    // DELETE http://localhost:8080/api/users/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}