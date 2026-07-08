package com.subscript.subscription.api.wrapper.request;

import com.subscript.subscription.api.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    private Integer userId;

    private String companyName;

    private String contactPerson;

    private String email;

    private String phone;

    private String address;

    private Customer.Status status;
}