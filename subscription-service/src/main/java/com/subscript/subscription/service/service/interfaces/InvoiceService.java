package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.service.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    Invoice createInvoice(Invoice invoice);
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByCustomer(Integer customerId);
    List<Invoice> getInvoicesByStatus(String status);
    Invoice getInvoiceById(Integer id);
    Invoice markAsPaid(Integer id);
    Invoice markAsOverdue(Integer id);
    Invoice updateInvoice(Integer id, Invoice updated);
    void deleteInvoice(Integer id);
}
