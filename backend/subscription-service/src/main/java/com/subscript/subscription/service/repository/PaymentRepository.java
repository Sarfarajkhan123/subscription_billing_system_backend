package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomer_CustomerId(Integer customerId);
    List<Payment> findByInvoice_InvoiceId(Integer invoiceId);
    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
}
