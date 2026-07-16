package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByCustomer_CustomerIdOrderByCreatedAtDesc(Integer customerId);
}
