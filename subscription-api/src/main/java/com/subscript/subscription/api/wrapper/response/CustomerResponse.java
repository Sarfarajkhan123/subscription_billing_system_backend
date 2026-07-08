package com.subscript.subscription.api.wrapper.response;

import com.subscript.subscription.api.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Integer customerId;

    private Integer userId;

    private String companyName;

    private String contactPerson;

    private String email;

    private String phone;

    private String address;

    private Customer.Status status;

    private LocalDateTime createdAt;
}