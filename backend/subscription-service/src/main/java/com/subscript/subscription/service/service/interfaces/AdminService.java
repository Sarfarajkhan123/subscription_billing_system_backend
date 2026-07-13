package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.EmployeeRequest;
import com.subscript.subscription.api.wrapper.request.ResetPasswordRequest;
import com.subscript.subscription.api.wrapper.request.UpdateUserStatusRequest;
import com.subscript.subscription.api.wrapper.response.UserResponse;

import java.util.List;

public interface AdminService {

    UserResponse createEmployee(EmployeeRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer userId);

    UserResponse updateUserStatus(
            Integer userId,
            UpdateUserStatusRequest request);

    void resetPassword(
            Integer userId,
            ResetPasswordRequest request);
}
