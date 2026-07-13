package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.LoginRequest;
import com.subscript.subscription.api.wrapper.request.RegisterRequest;
import com.subscript.subscription.api.wrapper.response.LoginResponse;
import com.subscript.subscription.api.wrapper.response.RegisterResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {

    ResponseEntity<RegisterResponse> register(RegisterRequest request);

    ResponseEntity<LoginResponse> login(LoginRequest request);
}