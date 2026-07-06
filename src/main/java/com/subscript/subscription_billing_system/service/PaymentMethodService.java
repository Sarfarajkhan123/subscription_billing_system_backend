package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Customer;
import com.subscript.subscription_billing_system.entity.PaymentMethod;
import com.subscript.subscription_billing_system.repository.CustomerRepository;
import com.subscript.subscription_billing_system.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;

    // ADD a payment method for a customer
    public PaymentMethod addPaymentMethod(Integer customerId, PaymentMethod method) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        method.setCustomer(customer);

        // If this is the first card, auto-set as default
        List<PaymentMethod> existing = paymentMethodRepository
                .findByCustomer_CustomerId(customerId);
        if (existing.isEmpty()) {
            method.setIsDefault(true);
        }

        return paymentMethodRepository.save(method);
    }

    // GET all payment methods for a customer
    public List<PaymentMethod> getMethodsByCustomer(Integer customerId) {
        return paymentMethodRepository.findByCustomer_CustomerId(customerId);
    }

    // GET one payment method by ID
    public PaymentMethod getMethodById(Integer id) {
        return paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment method not found: " + id));
    }

    // SET a specific card as default (unsets all others first)
    public PaymentMethod setDefault(Integer customerId, Integer methodId) {
        List<PaymentMethod> all = paymentMethodRepository
                .findByCustomer_CustomerId(customerId);

        // Unset all
        for (PaymentMethod m : all) {
            m.setIsDefault(false);
            paymentMethodRepository.save(m);
        }

        // Set the chosen one
        PaymentMethod chosen = getMethodById(methodId);
        chosen.setIsDefault(true);
        return paymentMethodRepository.save(chosen);
    }

    // DELETE a payment method
    public void deleteMethod(Integer id) {
        getMethodById(id);
        paymentMethodRepository.deleteById(id);
    }
}