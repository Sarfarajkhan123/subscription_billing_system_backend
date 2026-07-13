package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.SupportTicket;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SupportTicketController {

        ResponseEntity<SupportTicket> createTicket(
                        Integer customerId,
                        SupportTicket ticket);

        ResponseEntity<List<SupportTicket>> getAllTickets();

        ResponseEntity<List<SupportTicket>> getByCustomer(
                        Integer customerId);

        ResponseEntity<List<SupportTicket>> getByStatus(
                        String status);

        ResponseEntity<SupportTicket> getById(
                        Integer id);

        ResponseEntity<SupportTicket> assignTicket(
                        Integer id,
                        Integer agentId);

        ResponseEntity<SupportTicket> resolveTicket(
                        Integer id);

        ResponseEntity<SupportTicket> closeTicket(
                        Integer id);

        ResponseEntity<SupportTicket> updateTicket(
                        Integer id,
                        SupportTicket updated);

        ResponseEntity<String> deleteTicket(
                        Integer id);

}