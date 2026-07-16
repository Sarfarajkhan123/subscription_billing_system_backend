package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SupportTicketController;
import com.subscript.subscription.api.wrapper.request.SupportTicketRequest;
import com.subscript.subscription.api.wrapper.request.TicketStatusUpdateRequest;
import com.subscript.subscription.api.wrapper.response.SupportTicketResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import com.subscript.subscription.service.service.interfaces.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Support-ticket endpoints. Coarse role access is enforced in
 * SecurityBeansConfig; this controller adds the per-customer ownership check so
 * a CUSTOMER can only read their OWN tickets.
 *
 *  - CUSTOMER  : create + view own
 *  - SUPPORT   : view all + update status
 *  - IT_ADMIN  : view all + update status (cannot create)
 *  - FINANCE / PRODUCT : no access
 */
@RestController
@RequestMapping("/api/support/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SupportTicketControllerImpl implements SupportTicketController {

    private final SupportTicketService ticketService;
    private final CustomerService customerService;

    // POST /api/support/tickets — CUSTOMER only (customerId from JWT).
    @Override
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(
            @RequestBody SupportTicketRequest request) {
        Integer customerId = currentCustomerId();
        return ResponseEntity.ok(ticketService.createTicket(customerId, request));
    }

    // GET /api/support/tickets — SUPPORT / IT_ADMIN (all tickets).
    @Override
    @GetMapping
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // GET /api/support/tickets/my — CUSTOMER's own tickets.
    @Override
    @GetMapping("/my")
    public ResponseEntity<List<SupportTicketResponse>> getMyTickets() {
        Integer customerId = currentCustomerId();
        return ResponseEntity.ok(ticketService.getTicketsByCustomer(customerId));
    }

    // GET /api/support/tickets/{id} — SUPPORT / IT_ADMIN any; CUSTOMER own only.
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> getTicketById(
            @PathVariable Integer id) {
        SupportTicketResponse ticket = ticketService.getTicketById(id);
        enforceOwnership(ticket.getCustomerId());
        return ResponseEntity.ok(ticket);
    }

    // PUT /api/support/tickets/{id}/status — SUPPORT / IT_ADMIN.
    @Override
    @PutMapping("/{id}/status")
    public ResponseEntity<SupportTicketResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody TicketStatusUpdateRequest request) {
        return ResponseEntity.ok(ticketService.updateStatus(id, request.getStatus()));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Resolve the signed-in customer's id from the JWT. */
    private Integer currentCustomerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            return customerService.getMyProfile(auth.getName()).getCustomerId();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only customers can perform this action.");
        }
    }

    /**
     * A CUSTOMER may only access their OWN ticket; SUPPORT / IT_ADMIN are
     * privileged (any ticket). Identity comes from the JWT.
     */
    private void enforceOwnership(Integer customerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean privileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPPORT")
                        || a.getAuthority().equals("ROLE_IT_ADMIN"));
        if (privileged) {
            return;
        }
        Integer ownId;
        try {
            ownId = customerService.getMyProfile(auth.getName()).getCustomerId();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only access your own tickets.");
        }
        if (!ownId.equals(customerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only access your own tickets.");
        }
    }
}
