package com.subscript.subscription_billing_system.dto.request;

import lombok.Data;

@Data
public class CreateTicketRequest {
    private String subject;
    private String description;
    private String category;
    private String priority;
}
