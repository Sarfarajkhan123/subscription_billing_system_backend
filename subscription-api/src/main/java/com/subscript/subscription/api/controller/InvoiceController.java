package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Invoice;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvoiceController {

    ResponseEntity<Invoice> createInvoice(Invoice invoice);

    ResponseEntity<List<Invoice>> getAllInvoices();

    ResponseEntity<List<Invoice>> getByCustomer(Integer customerId);

    ResponseEntity<List<Invoice>> getByStatus(String status);

    ResponseEntity<Invoice> getById(Integer id);

    ResponseEntity<Invoice> markAsPaid(Integer id);

    ResponseEntity<Invoice> markAsOverdue(Integer id);

    ResponseEntity<Invoice> updateInvoice(Integer id, Invoice updated);

    ResponseEntity<String> deleteInvoice(Integer id);

}