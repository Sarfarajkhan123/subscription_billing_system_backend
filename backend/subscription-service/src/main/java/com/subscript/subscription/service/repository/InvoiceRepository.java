package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByCustomer_CustomerId(Integer customerId);
    List<Invoice> findByCustomer_CustomerIdOrderByIssuedAtDesc(Integer customerId);
    List<Invoice> findByStatus(Invoice.Status status);
    long countByStatus(Invoice.Status status);

    // Duplicate-invoice guard: one invoice per subscription + billing period.
    Optional<Invoice> findFirstBySubscription_SubscriptionIdAndPeriodStartAndPeriodEnd(
            Integer subscriptionId, LocalDate periodStart, LocalDate periodEnd);

    // Most recent invoice that carried a coupon — reused so recurring cycle
    // invoices keep applying the subscription's discount.
    Optional<Invoice> findTopBySubscription_SubscriptionIdAndDiscountIsNotNullOrderByIssuedAtDesc(
            Integer subscriptionId);
}
