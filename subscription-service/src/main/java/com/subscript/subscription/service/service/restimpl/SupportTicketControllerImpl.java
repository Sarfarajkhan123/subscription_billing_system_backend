package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.SupportTicketController;
import com.subscript.subscription.api.model.SupportTicket;
import com.subscript.subscription.service.service.interfaces.SupportTicketService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SupportTicketControllerImpl implements SupportTicketController {

        private final SupportTicketService ticketService;

        // POST /api/tickets?customerId=1
        @Override
        @PostMapping
        public ResponseEntity<SupportTicket> createTicket(
                        @RequestParam Integer customerId,
                        @RequestBody SupportTicket ticket) {

                return ResponseEntity.ok(
                                ticketService.createTicket(customerId, ticket));
        }

        // GET /api/tickets
        @Override
        @GetMapping
        public ResponseEntity<List<SupportTicket>> getAllTickets() {

                return ResponseEntity.ok(
                                ticketService.getAllTickets());
        }

        // GET /api/tickets/customer/{customerId}
        @Override
        @GetMapping("/customer/{customerId}")
        public ResponseEntity<List<SupportTicket>> getByCustomer(
                        @PathVariable Integer customerId) {

                return ResponseEntity.ok(
                                ticketService.getTicketsByCustomer(customerId));
        }

        // GET /api/tickets/status/{status}
        @Override
        @GetMapping("/status/{status}")
        public ResponseEntity<List<SupportTicket>> getByStatus(
                        @PathVariable String status) {

                return ResponseEntity.ok(
                                ticketService.getTicketsByStatus(status));
        }

        // GET /api/tickets/{id}
        @Override
        @GetMapping("/{id}")
        public ResponseEntity<SupportTicket> getById(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                ticketService.getTicketById(id));
        }

        // PUT /api/tickets/{id}/assign?agentId=2
        @Override
        @PutMapping("/{id}/assign")
        public ResponseEntity<SupportTicket> assignTicket(
                        @PathVariable Integer id,
                        @RequestParam Integer agentId) {

                return ResponseEntity.ok(
                                ticketService.assignTicket(id, agentId));
        }

        // PUT /api/tickets/{id}/resolve
        @Override
        @PutMapping("/{id}/resolve")
        public ResponseEntity<SupportTicket> resolveTicket(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                ticketService.resolveTicket(id));
        }

        // PUT /api/tickets/{id}/close
        @Override
        @PutMapping("/{id}/close")
        public ResponseEntity<SupportTicket> closeTicket(
                        @PathVariable Integer id) {

                return ResponseEntity.ok(
                                ticketService.closeTicket(id));
        }

        // PUT /api/tickets/{id}
        @Override
        @PutMapping("/{id}")
        public ResponseEntity<SupportTicket> updateTicket(
                        @PathVariable Integer id,
                        @RequestBody SupportTicket updated) {

                return ResponseEntity.ok(
                                ticketService.updateTicket(id, updated));
        }

        // DELETE /api/tickets/{id}
        @Override
        @DeleteMapping("/{id}")
        public ResponseEntity<String> deleteTicket(
                        @PathVariable Integer id) {

                ticketService.deleteTicket(id);

                return ResponseEntity.ok("Ticket deleted");
        }
}