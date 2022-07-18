package com.volume.volumewebhookjavaconsumer.rest.data;

import java.math.BigDecimal;

public record PaymentRequestDto(
    BigDecimal amount,
    String currency,
    String reference
) {
}
