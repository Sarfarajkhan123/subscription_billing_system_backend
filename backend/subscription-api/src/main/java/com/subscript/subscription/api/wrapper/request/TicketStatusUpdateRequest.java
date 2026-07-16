package com.subscript.subscription.api.wrapper.request;

import lombok.Data;

/** Body Support / IT Admin sends to change a ticket's status. */
@Data
public class TicketStatusUpdateRequest {
    private String status;   // OPEN | IN_PROGRESS | RESOLVED | CLOSED
}
