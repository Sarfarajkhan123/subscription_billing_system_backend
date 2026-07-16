package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.wrapper.request.SupportTicketRequest;
import com.subscript.subscription.api.wrapper.response.SupportTicketResponse;

import java.util.List;

public interface SupportTicketService {

    // Customer opens a ticket for themselves.
    SupportTicketResponse createTicket(Integer customerId, SupportTicketRequest request);

    // Support / IT Admin: all tickets.
    List<SupportTicketResponse> getAllTickets();

    // A customer's own tickets.
    List<SupportTicketResponse> getTicketsByCustomer(Integer customerId);

    SupportTicketResponse getTicketById(Integer id);

    // Support / IT Admin update the ticket status.
    SupportTicketResponse updateStatus(Integer id, String status);
}
