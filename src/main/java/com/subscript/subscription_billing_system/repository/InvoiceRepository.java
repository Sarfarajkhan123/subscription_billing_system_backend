package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByCustomer_CustomerId(Integer customerId);
    List<Invoice> findByCustomer_CustomerIdOrderByIssuedAtDesc(Integer customerId);
    List<Invoice> findByStatus(Invoice.Status status);
    long countByStatus(Invoice.Status status);
}
