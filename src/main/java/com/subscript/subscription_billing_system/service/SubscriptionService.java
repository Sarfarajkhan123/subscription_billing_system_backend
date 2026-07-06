package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.*;
import com.subscript.subscription_billing_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionPlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;

    // SUBSCRIBE — creates subscription AND auto-generates invoice
    public Subscription subscribe(Integer customerId, Integer planId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));

        // Create the subscription
        Subscription sub = new Subscription();
        sub.setCustomer(customer);
        sub.setService(plan.getService());
        sub.setPlan(plan);
        sub.setStatus(Subscription.Status.active);
        sub.setApiKey(UUID.randomUUID().toString().replace("-", "").substring(0, 32));
        sub.setStartDate(LocalDate.now());
        sub.setRenewalDate(LocalDate.now().plusMonths(1));
        sub = subscriptionRepository.save(sub);

        // Auto-generate the first invoice immediately
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setSubscription(sub);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setBaseAmount(plan.getBasePrice());
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(plan.getBasePrice());
        invoice.setPeriodStart(LocalDate.now());
        invoice.setPeriodEnd(LocalDate.now().plusMonths(1));
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(Invoice.Status.pending);
        invoiceRepository.save(invoice);

        return sub;
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getSubscriptionsByCustomer(Integer customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId);
    }

    public Subscription getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
    }

    // UPGRADE — change the plan on an existing subscription
    public Subscription upgradePlan(Integer subscriptionId, Integer newPlanId) {
        Subscription sub = getSubscriptionById(subscriptionId);
        SubscriptionPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + newPlanId));
        sub.setPlan(newPlan);
        return subscriptionRepository.save(sub);
    }

    // CANCEL
    public Subscription cancelSubscription(Integer id) {
        Subscription sub = getSubscriptionById(id);
        sub.setStatus(Subscription.Status.cancelled);
        sub.setCancelledAt(LocalDateTime.now());
        return subscriptionRepository.save(sub);
    }

    public void deleteSubscription(Integer id) {
        getSubscriptionById(id);
        subscriptionRepository.deleteById(id);
    }
}