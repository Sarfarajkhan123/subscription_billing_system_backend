package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByUser_UserId(Integer userId);
    List<Customer> findByCompanyNameContainingIgnoreCase(String keyword);
}
