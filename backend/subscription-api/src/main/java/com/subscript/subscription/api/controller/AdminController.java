package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.EmployeeRequest;
import com.subscript.subscription.api.wrapper.request.ResetPasswordRequest;
import com.subscript.subscription.api.wrapper.request.UpdateUserStatusRequest;
import com.subscript.subscription.api.wrapper.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AdminController {

    ResponseEntity<UserResponse> createEmployee(
            @RequestBody EmployeeRequest request);

    ResponseEntity<List<UserResponse>> getAllUsers();

    ResponseEntity<UserResponse> getUserById(
            @PathVariable Integer userId);

    ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody UpdateUserStatusRequest request);

    ResponseEntity<String> resetPassword(
            @PathVariable Integer userId,
            @RequestBody ResetPasswordRequest request);
}
