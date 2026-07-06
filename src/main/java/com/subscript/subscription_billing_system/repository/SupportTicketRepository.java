package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Integer> {
    List<SupportTicket> findByCustomer_CustomerId(Integer customerId);

    List<SupportTicket> findByStatus(SupportTicket.Status status);

    List<SupportTicket> findByAssignedTo_UserId(Integer userId);
}