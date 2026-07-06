package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Customer;
import com.subscript.subscription_billing_system.entity.SupportTicket;
import com.subscript.subscription_billing_system.entity.User;
import com.subscript.subscription_billing_system.repository.CustomerRepository;
import com.subscript.subscription_billing_system.repository.SupportTicketRepository;
import com.subscript.subscription_billing_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SupportTicket createTicket(Integer customerId, SupportTicket ticket) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        ticket.setCustomer(customer);
        ticket.setTicketNumber("TKT-" + System.currentTimeMillis());
        ticket.setStatus(SupportTicket.Status.open);
        return ticketRepository.save(ticket);
    }

    public List<SupportTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<SupportTicket> getTicketsByCustomer(Integer customerId) {
        return ticketRepository.findByCustomer_CustomerId(customerId);
    }

    public List<SupportTicket> getTicketsByStatus(String status) {
        SupportTicket.Status s = SupportTicket.Status.valueOf(status.toLowerCase());
        return ticketRepository.findByStatus(s);
    }

    public SupportTicket getTicketById(Integer id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
    }

    // Assign ticket to a support agent
    public SupportTicket assignTicket(Integer ticketId, Integer agentUserId) {
        SupportTicket ticket = getTicketById(ticketId);
        User agent = userRepository.findById(agentUserId)
                .orElseThrow(() -> new RuntimeException("Agent not found: " + agentUserId));
        ticket.setAssignedTo(agent);
        ticket.setStatus(SupportTicket.Status.in_progress);
        return ticketRepository.save(ticket);
    }

    // Resolve a ticket
    public SupportTicket resolveTicket(Integer id) {
        SupportTicket ticket = getTicketById(id);
        ticket.setStatus(SupportTicket.Status.resolved);
        ticket.setResolvedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    // Close a ticket
    public SupportTicket closeTicket(Integer id) {
        SupportTicket ticket = getTicketById(id);
        ticket.setStatus(SupportTicket.Status.closed);
        return ticketRepository.save(ticket);
    }

    public SupportTicket updateTicket(Integer id, SupportTicket updated) {
        SupportTicket existing = getTicketById(id);
        if (updated.getStatus() != null)
            existing.setStatus(updated.getStatus());
        if (updated.getPriority() != null)
            existing.setPriority(updated.getPriority());
        return ticketRepository.save(existing);
    }

    public void deleteTicket(Integer id) {
        getTicketById(id);
        ticketRepository.deleteById(id);
    }
}