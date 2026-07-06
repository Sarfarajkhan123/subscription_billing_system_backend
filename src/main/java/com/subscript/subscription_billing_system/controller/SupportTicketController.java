package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.SupportTicket;
import com.subscript.subscription_billing_system.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    // POST /api/tickets?customerId=1
    @PostMapping
    public ResponseEntity<SupportTicket> createTicket(
            @RequestParam Integer customerId,
            @RequestBody SupportTicket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(customerId, ticket));
    }

    // GET /api/tickets
    @GetMapping
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // GET /api/tickets/customer/1
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SupportTicket>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(ticketService.getTicketsByCustomer(customerId));
    }

    // GET /api/tickets/status/open
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportTicket>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
    }

    // GET /api/tickets/1
    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    // PUT /api/tickets/1/assign?agentId=2
    @PutMapping("/{id}/assign")
    public ResponseEntity<SupportTicket> assignTicket(
            @PathVariable Integer id,
            @RequestParam Integer agentId) {
        return ResponseEntity.ok(ticketService.assignTicket(id, agentId));
    }

    // PUT /api/tickets/1/resolve
    @PutMapping("/{id}/resolve")
    public ResponseEntity<SupportTicket> resolveTicket(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.resolveTicket(id));
    }

    // PUT /api/tickets/1/close
    @PutMapping("/{id}/close")
    public ResponseEntity<SupportTicket> closeTicket(@PathVariable Integer id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }

    // PUT /api/tickets/1
    @PutMapping("/{id}")
    public ResponseEntity<SupportTicket> updateTicket(
            @PathVariable Integer id,
            @RequestBody SupportTicket updated) {
        return ResponseEntity.ok(ticketService.updateTicket(id, updated));
    }

    // DELETE /api/tickets/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable Integer id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok("Ticket deleted");
    }
}