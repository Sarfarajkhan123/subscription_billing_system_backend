package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
