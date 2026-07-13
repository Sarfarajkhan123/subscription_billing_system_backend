package com.subscript.subscription.api.wrapper.request;

import com.subscript.subscription.api.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required")
    private User.Status status;
}
