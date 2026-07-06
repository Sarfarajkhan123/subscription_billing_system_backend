package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomer_CustomerId(Integer customerId);
    List<Payment> findByInvoice_InvoiceId(Integer invoiceId);
}
