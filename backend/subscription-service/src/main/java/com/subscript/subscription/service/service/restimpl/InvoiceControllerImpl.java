package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.InvoiceController;
import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import com.subscript.subscription.service.service.interfaces.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;
    private final CustomerService customerService;

    @Override
    @PostMapping("/generate")
    public ResponseEntity<InvoiceResponse> generateInvoice(
            @RequestBody InvoiceGenerateRequest request) {

        return ResponseEntity.ok(
                invoiceService.generateInvoice(request));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {

        return ResponseEntity.ok(
                invoiceService.getAllInvoices());
    }

    @Override
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<InvoiceResponse>> getByCustomer(
            @PathVariable Integer customerId) {

        enforceInvoiceOwnership(customerId);

        return ResponseEntity.ok(
                invoiceService.getInvoicesByCustomer(customerId));
    }

    @Override
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                invoiceService.getInvoicesByStatus(status));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(
            @PathVariable Integer id) {

        InvoiceResponse invoice = invoiceService.getInvoiceById(id);
        enforceInvoiceOwnership(invoice.getCustomerId());

        return ResponseEntity.ok(invoice);
    }

    @Override
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<InvoiceResponse> markAsPaid(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                invoiceService.markAsPaid(id));
    }

    @Override
    @PutMapping("/{id}/mark-overdue")
    public ResponseEntity<InvoiceResponse> markAsOverdue(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                invoiceService.markAsOverdue(id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(
            @PathVariable Integer id) {

        invoiceService.deleteInvoice(id);

        return ResponseEntity.ok("Invoice deleted successfully.");
    }

    /**
     * Ownership guard: a CUSTOMER may only read their OWN invoices. Finance and
     * IT Admin are privileged (full access), so their behaviour is unchanged.
     * Identity comes from the JWT via the Spring Security context.
     */
    private void enforceInvoiceOwnership(Integer customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean privileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FINANCE")
                        || a.getAuthority().equals("ROLE_IT_ADMIN"));
        if (privileged) {
            return;
        }

        Integer ownCustomerId;
        try {
            ownCustomerId = customerService.getMyProfile(auth.getName())
                    .getCustomerId();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own invoices.");
        }
        if (!ownCustomerId.equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own invoices.");
        }
    }
}