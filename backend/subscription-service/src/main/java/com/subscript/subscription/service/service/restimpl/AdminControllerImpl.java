package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.AdminController;
import com.subscript.subscription.api.wrapper.request.EmployeeRequest;
import com.subscript.subscription.api.wrapper.request.ResetPasswordRequest;
import com.subscript.subscription.api.wrapper.request.UpdateUserStatusRequest;
import com.subscript.subscription.api.wrapper.response.UserResponse;
import com.subscript.subscription.service.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminControllerImpl implements AdminController {

    private final AdminService adminService;

    @Override
    @PostMapping("/users")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public ResponseEntity<UserResponse> createEmployee(
            @RequestBody EmployeeRequest request) {

        return ResponseEntity.ok(
                adminService.createEmployee(request));
    }

    @Override
    @GetMapping("/users")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                adminService.getAllUsers());
    }

    @Override
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                adminService.getUserById(userId));
    }

    @Override
    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(
                adminService.updateUserStatus(userId, request));
    }

    @Override
    @PutMapping("/users/{userId}/reset-password")
    @PreAuthorize("hasRole('IT_ADMIN')")
    public ResponseEntity<String> resetPassword(
            @PathVariable Integer userId,
            @RequestBody ResetPasswordRequest request) {

        adminService.resetPassword(userId, request);

        return ResponseEntity.ok("Password reset successfully.");
    }
}
