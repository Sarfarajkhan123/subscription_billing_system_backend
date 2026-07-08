package com.subscript.subscription.service.service.interfaces;

import java.util.List;

import com.subscript.subscription.api.wrapper.request.CustomerRequest;
import com.subscript.subscription.api.wrapper.response.CustomerResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Integer id);

    CustomerResponse getCustomerByEmail(String email);

    List<CustomerResponse> searchByCompanyName(String keyword);

    CustomerResponse updateCustomer(Integer id, CustomerRequest request);

    void deleteCustomer(Integer id);
}