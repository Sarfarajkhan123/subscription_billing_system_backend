package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.UserController;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserControllerImpl implements UserController {

        private final UserService userService;

        // POST /api/users
        @Override
        @PostMapping
        public ResponseEntity<User> createUser(
                        @RequestBody User user) {

                User saved = userService.createUser(user);
                return ResponseEntity.ok(saved);
        }

        // GET /api/users
        @Override
        @GetMapping
        public ResponseEntity<List<User>> getAllUsers() {

                return ResponseEntity.ok(
                                userService.getAllUsers());
        }

        // GET /api/users/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                userService.getUserById(id));
        }

        // GET /api/users/email/{email}
        @Override
        @GetMapping("/email/{email}")
        public ResponseEntity<User> getUserByEmail(
                        @PathVariable String email) {

                return ResponseEntity.ok(
                                userService.getUserByEmail(email));
        }

        // PUT /api/users/{id}
        @Override
        @PutMapping("/{id}")
        public ResponseEntity<User> updateUser(
                        @PathVariable Integer id,
                        @RequestBody User updatedData) {

                return ResponseEntity.ok(
                                userService.updateUser(id, updatedData));
        }

        // PUT /api/users/{id}/deactivate
        @Override
        @PutMapping("/{id}/deactivate")
        public ResponseEntity<User> deactivateUser(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                userService.deactivateUser(id));
        }

        // PUT /api/users/{id}/activate
        @Override
        @PutMapping("/{id}/activate")
        public ResponseEntity<User> activateUser(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                userService.activateUser(id));
        }

        // DELETE /api/users/{id}
        @Override
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteUser(
                        @PathVariable Integer id) {

                userService.deleteUser(id);

                return ResponseEntity.ok("User deleted successfully");
        }
}