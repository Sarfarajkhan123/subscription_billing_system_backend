package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    // Generate invoice
    InvoiceResponse generateInvoice(InvoiceGenerateRequest request);

    // Get all invoices
    List<InvoiceResponse> getAllInvoices();

    // Get invoices by customer
    List<InvoiceResponse> getInvoicesByCustomer(Integer customerId);

    // Get invoices by status
    List<InvoiceResponse> getInvoicesByStatus(String status);

    // Get invoice by id
    InvoiceResponse getInvoiceById(Integer id);

    // Mark invoice as paid
    InvoiceResponse markAsPaid(Integer id);

    // Mark invoice as overdue
    InvoiceResponse markAsOverdue(Integer id);

    // Delete invoice
    void deleteInvoice(Integer id);
}