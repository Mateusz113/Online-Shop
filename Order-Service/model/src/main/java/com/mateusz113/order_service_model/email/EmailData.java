package com.mateusz113.order_service_model.email;

import lombok.Builder;

@Builder
public record EmailData(
        String recipientEmail,
        String subject,
        String message
) {
}
