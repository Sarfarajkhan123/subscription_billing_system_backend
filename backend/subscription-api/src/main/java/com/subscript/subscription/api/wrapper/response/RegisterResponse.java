package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private String message;

    private Integer userId;

    private Integer customerId;     // null for employee roles

    private String email;

    private String firstName;

    private String role;
}
