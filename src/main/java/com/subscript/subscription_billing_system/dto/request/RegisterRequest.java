package com.subscript.subscription_billing_system.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String companyName;
    private String phone;
}
