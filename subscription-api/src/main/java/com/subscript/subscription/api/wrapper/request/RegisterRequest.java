package com.subscript.subscription.api.wrapper.request;

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
