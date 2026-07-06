package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Customer;
import com.subscript.subscription_billing_system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new RuntimeException("Customer with this email already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public List<Customer> searchByCompanyName(String keyword) {
        return customerRepository.findByCompanyNameContainingIgnoreCase(keyword);
    }

    public Customer updateCustomer(Integer id, Customer updated) {
        Customer existing = getCustomerById(id);
        if (updated.getCompanyName() != null)
            existing.setCompanyName(updated.getCompanyName());
        if (updated.getContactPerson() != null)
            existing.setContactPerson(updated.getContactPerson());
        if (updated.getPhone() != null)
            existing.setPhone(updated.getPhone());
        if (updated.getAddress() != null)
            existing.setAddress(updated.getAddress());
        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(Integer id) {
        getCustomerById(id);
        customerRepository.deleteById(id);
    }
}