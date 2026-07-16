package com.subscript.subscription.api.wrapper.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Support ticket view. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponse {
    private Integer ticketId;
    private Integer customerId;
    private String subject;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
