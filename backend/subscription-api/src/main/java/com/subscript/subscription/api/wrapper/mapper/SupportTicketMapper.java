package com.subscript.subscription.api.wrapper.mapper;

import com.subscript.subscription.api.model.SupportTicket;
import com.subscript.subscription.api.wrapper.response.SupportTicketResponse;

public final class SupportTicketMapper {

    private SupportTicketMapper() {
    }

    public static SupportTicketResponse toResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getTicketId(),
                ticket.getCustomer() != null ? ticket.getCustomer().getCustomerId() : null,
                ticket.getSubject(),
                ticket.getDescription(),
                ticket.getStatus() != null ? ticket.getStatus().name() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt());
    }
}
