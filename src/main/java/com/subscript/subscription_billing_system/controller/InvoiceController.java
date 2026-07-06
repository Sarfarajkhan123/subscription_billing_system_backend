package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.Invoice;
import com.subscript.subscription_billing_system.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceController {

    private final InvoiceService invoiceService;

    // POST /api/invoices (manual invoice — for Finance team)
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    // GET /api/invoices
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET /api/invoices/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCustomer(customerId));
    }

    // GET /api/invoices/status/pending
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    // GET /api/invoices/1
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // PUT /api/invoices/1/mark-paid
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    // PUT /api/invoices/1/mark-overdue
    @PutMapping("/{id}/mark-overdue")
    public ResponseEntity<Invoice> markAsOverdue(@PathVariable Integer id) {
        return ResponseEntity.ok(invoiceService.markAsOverdue(id));
    }

    // PUT /api/invoices/1
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(
            @PathVariable Integer id,
            @RequestBody Invoice updated) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, updated));
    }

    // DELETE /api/invoices/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted");
    }
}