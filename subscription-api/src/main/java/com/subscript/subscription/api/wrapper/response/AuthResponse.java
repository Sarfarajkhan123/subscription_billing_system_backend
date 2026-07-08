package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String message;

    private String email;

    private String firstName;

    private String role;

}