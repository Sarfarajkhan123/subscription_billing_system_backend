package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.LoginRequest;
import com.subscript.subscription.api.wrapper.request.RegisterRequest;
import com.subscript.subscription.api.wrapper.response.AuthResponse;
import org.springframework.http.ResponseEntity;

public interface AuthController {

    ResponseEntity<AuthResponse> register(RegisterRequest request);

    ResponseEntity<AuthResponse> login(LoginRequest request);

}