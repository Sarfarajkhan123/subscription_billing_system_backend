package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.service.service.interfaces.PaymentMethodService;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.PaymentMethod;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {

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