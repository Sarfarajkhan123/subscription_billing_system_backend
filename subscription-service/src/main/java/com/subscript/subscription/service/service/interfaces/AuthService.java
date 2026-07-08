package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.LoginRequest;
import com.subscript.subscription.api.wrapper.request.RegisterRequest;
import com.subscript.subscription.api.wrapper.response.AuthResponse;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
