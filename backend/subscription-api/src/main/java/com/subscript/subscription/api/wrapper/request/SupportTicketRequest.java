package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

/** Body a customer sends to open a support ticket. */
@Data
public class SupportTicketRequest {
    private String subject;
    private String description;
}
