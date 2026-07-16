package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.SupportTicketRequest;
import com.subscript.subscription.api.wrapper.request.TicketStatusUpdateRequest;
import com.subscript.subscription.api.wrapper.response.SupportTicketResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupportTicketController {

    ResponseEntity<SupportTicketResponse> createTicket(SupportTicketRequest request);

    ResponseEntity<List<SupportTicketResponse>> getAllTickets();

    ResponseEntity<List<SupportTicketResponse>> getMyTickets();

    ResponseEntity<SupportTicketResponse> getTicketById(Integer id);

    ResponseEntity<SupportTicketResponse> updateStatus(
            Integer id, TicketStatusUpdateRequest request);
}
