package com.subscript.subscription_billing_system.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
