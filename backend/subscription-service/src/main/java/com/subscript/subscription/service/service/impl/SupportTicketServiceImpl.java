package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.SupportTicket;
import com.subscript.subscription.api.wrapper.mapper.SupportTicketMapper;
import com.subscript.subscription.api.wrapper.request.SupportTicketRequest;
import com.subscript.subscription.api.wrapper.response.SupportTicketResponse;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.SupportTicketRepository;
import com.subscript.subscription.service.repository.UserRepository;
import com.subscript.subscription.service.service.interfaces.AuditLogService;
import com.subscript.subscription.service.service.interfaces.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public SupportTicketResponse createTicket(Integer customerId, SupportTicketRequest request) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found: " + customerId));

        if (request.getSubject() == null || request.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subject is required.");
        }

        SupportTicket ticket = new SupportTicket();
        ticket.setCustomer(customer);
        ticket.setSubject(request.getSubject().trim());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(SupportTicket.TicketStatus.OPEN);

        SupportTicket saved = ticketRepository.save(ticket);

        // Audit: the customer (owner) is the actor for a create.
        writeAudit(customer.getUser() != null ? customer.getUser().getUserId() : null,
                "TICKET_CREATED", saved.getTicketId(),
                "Ticket created: " + saved.getSubject());

        return SupportTicketMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(SupportTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getTicketsByCustomer(Integer customerId) {
        return ticketRepository.findByCustomer_CustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(SupportTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketById(Integer id) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found: " + id));
        return SupportTicketMapper.toResponse(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse updateStatus(Integer id, String status) {

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found: " + id));

        SupportTicket.TicketStatus newStatus;
        try {
            newStatus = SupportTicket.TicketStatus.valueOf(
                    status == null ? "" : status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed: OPEN, IN_PROGRESS, RESOLVED, CLOSED.");
        }

        ticket.setStatus(newStatus);
        SupportTicket saved = ticketRepository.save(ticket);

        // Audit: the acting staff user (from the JWT) is the actor for an update.
        writeAudit(currentUserId(), "TICKET_STATUS_UPDATED", saved.getTicketId(),
                "Status changed to " + newStatus);

        return SupportTicketMapper.toResponse(saved);
    }

    /** Resolve the acting user's id from the JWT (null if unresolved). */
    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(auth.getName())
                .map(u -> u.getUserId())
                .orElse(null);
    }

    /** Best-effort audit write — never let an audit failure break the operation. */
    private void writeAudit(Integer userId, String action, Integer ticketId, String description) {
        if (userId == null) {
            return;
        }
        try {
            auditLogService.log(userId, action, "SUPPORT_TICKET", ticketId, description);
        } catch (RuntimeException ignored) {
            // auditing is non-critical
        }
    }
}
