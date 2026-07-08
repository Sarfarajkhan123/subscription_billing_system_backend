package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

@Data
public class CreateTicketRequest {
    private String subject;
    private String description;
    private String category;
    private String priority;
}
