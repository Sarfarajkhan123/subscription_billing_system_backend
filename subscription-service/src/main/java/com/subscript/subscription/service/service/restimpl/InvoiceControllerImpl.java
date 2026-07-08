package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.InvoiceController;
import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.service.service.interfaces.InvoiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;

    // POST /api/invoices
    @Override
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Invoice invoice) {

        return ResponseEntity.ok(invoiceService.createInvoice(invoice));
    }

    // GET /api/invoices
    @Override
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {

        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET /api/invoices/customer/{customerId}
    @Override
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getByCustomer(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(invoiceService.getInvoicesByCustomer(customerId));
    }

    // GET /api/invoices/status/{status}
    @Override
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Invoice>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    // GET /api/invoices/{id}
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // PUT /api/invoices/{id}/mark-paid
    @Override
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<Invoice> markAsPaid(
            @PathVariable Integer id) {

        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }

    // PUT /api/invoices/{id}/mark-overdue
    @Override
    @PutMapping("/{id}/mark-overdue")
    public ResponseEntity<Invoice> markAsOverdue(
            @PathVariable Integer id) {

        return ResponseEntity.ok(invoiceService.markAsOverdue(id));
    }

    // PUT /api/invoices/{id}
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(
            @PathVariable Integer id,
            @RequestBody Invoice updated) {

        return ResponseEntity.ok(invoiceService.updateInvoice(id, updated));
    }

    // DELETE /api/invoices/{id}
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable Integer id) {

        invoiceService.deleteInvoice(id);

        return ResponseEntity.ok("Invoice deleted");
    }
}