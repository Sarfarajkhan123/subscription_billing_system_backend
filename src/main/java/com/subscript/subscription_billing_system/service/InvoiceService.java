package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Invoice;
import com.subscript.subscription_billing_system.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice createInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByCustomer(Integer customerId) {
        return invoiceRepository.findByCustomer_CustomerIdOrderByIssuedAtDesc(customerId);
    }

    public List<Invoice> getInvoicesByStatus(String status) {
        Invoice.Status invoiceStatus = Invoice.Status.valueOf(status.toLowerCase());
        return invoiceRepository.findByStatus(invoiceStatus);
    }

    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
    }

    public Invoice markAsPaid(Integer id) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus(Invoice.Status.paid);
        invoice.setPaidAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public Invoice markAsOverdue(Integer id) {
        Invoice invoice = getInvoiceById(id);
        invoice.setStatus(Invoice.Status.overdue);
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Integer id, Invoice updated) {
        Invoice existing = getInvoiceById(id);
        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());
        if (updated.getNotes() != null)
            existing.setNotes(updated.getNotes());
        if (updated.getDueDate() != null)
            existing.setDueDate(updated.getDueDate());
        return invoiceRepository.save(existing);
    }

    public void deleteInvoice(Integer id) {
        getInvoiceById(id);
        invoiceRepository.deleteById(id);
    }
}