package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.wrapper.request.CustomerRequest;
import com.subscript.subscription.api.wrapper.response.CustomerResponse;

public class CustomerMapper {

    private CustomerMapper() {
    }

    public static Customer toEntity(CustomerRequest request) {

        Customer customer = new Customer();

        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }

        return customer;
    }

    public static CustomerResponse toResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setCustomerId(customer.getCustomerId());

        if (customer.getUser() != null) {
            response.setUserId(customer.getUser().getUserId());
        }

        response.setCompanyName(customer.getCompanyName());
        response.setContactPerson(customer.getContactPerson());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setAddress(customer.getAddress());
        response.setStatus(customer.getStatus());
        response.setCreatedAt(customer.getCreatedAt());

        return response;
    }
}