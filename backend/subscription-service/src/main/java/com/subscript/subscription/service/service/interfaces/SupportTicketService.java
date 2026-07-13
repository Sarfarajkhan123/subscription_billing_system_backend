package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.SupportTicket;
import com.subscript.subscription.api.model.User;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.SupportTicketRepository;
import com.subscript.subscription.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

public interface SupportTicketService {
    SupportTicket createTicket(Integer customerId, SupportTicket ticket);
    List<SupportTicket> getAllTickets();
    List<SupportTicket> getTicketsByCustomer(Integer customerId);
    List<SupportTicket> getTicketsByStatus(String status);
    SupportTicket getTicketById(Integer id);
    SupportTicket assignTicket(Integer ticketId, Integer agentUserId);
    SupportTicket resolveTicket(Integer id);
    SupportTicket closeTicket(Integer id);
    SupportTicket updateTicket(Integer id, SupportTicket updated);
    void deleteTicket(Integer id);
}
