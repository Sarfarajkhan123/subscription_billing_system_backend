package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.wrapper.mapper.CustomerMapper;
import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.api.wrapper.request.CustomerRequest;
import com.subscript.subscription.api.wrapper.response.CustomerResponse;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.UserRepository;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customerRepository.findByUser_UserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("Customer already exists for this user");
        }

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Customer with this email already exists");
        }

        Customer customer = CustomerMapper.toEntity(request);
        customer.setUser(user);

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerMapper.toResponse(savedCustomer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Integer id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByEmail(String email) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));

        return CustomerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> searchByCompanyName(String keyword) {

        return customerRepository
                .findByCompanyNameContainingIgnoreCase(keyword)
                .stream()
                .map(CustomerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(Integer id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }

        Customer updatedCustomer = customerRepository.save(customer);

        return CustomerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Integer id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customerRepository.delete(customer);
    }

    @Override
    public CustomerResponse getMyProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository
                .findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return CustomerMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse updateMyProfile(
            String email,
            CustomerRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository
                .findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Customer can update only these fields
        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        // Optional
        customer.setEmail(request.getEmail());

        Customer updatedCustomer = customerRepository.save(customer);

        return CustomerMapper.toResponse(updatedCustomer);
    }
}