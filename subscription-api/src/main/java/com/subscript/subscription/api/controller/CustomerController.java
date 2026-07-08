package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.wrapper.request.CustomerRequest;
import com.subscript.subscription.api.wrapper.response.CustomerResponse;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerController {

    ResponseEntity<CustomerResponse> createCustomer(CustomerRequest request);

    ResponseEntity<List<CustomerResponse>> getAllCustomers();

    ResponseEntity<CustomerResponse> getCustomerById(Integer id);

    ResponseEntity<CustomerResponse> getCustomerByEmail(String email);

    ResponseEntity<List<CustomerResponse>> searchCustomers(String keyword);

    ResponseEntity<CustomerResponse> updateCustomer(Integer id, CustomerRequest request);

    ResponseEntity<String> deleteCustomer(Integer id);

}