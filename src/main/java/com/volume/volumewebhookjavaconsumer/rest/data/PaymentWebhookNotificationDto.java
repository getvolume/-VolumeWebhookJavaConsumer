package com.volume.volumewebhookjavaconsumer.rest.data;

import java.util.UUID;

public record PaymentWebhookNotificationDto(
    UUID paymentId,
    UUID merchantPaymentId,
    VolumePaymentStatus paymentStatus,
    String errorDescription,
    PaymentRequestDto paymentRequest
) {
}
