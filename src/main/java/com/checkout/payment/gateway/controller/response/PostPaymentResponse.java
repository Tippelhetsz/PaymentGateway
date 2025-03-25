package com.checkout.payment.gateway.controller.response;

import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;

public record PostPaymentResponse (
        UUID id,
        PaymentStatus status,
        int cardNumberLastFour,
        int expiryMonth,
        int expiryYear,
        String currency,
        int amount
)
{
  @Override
  public String toString() {
    return "GetPaymentResponse{" +
            "id=" + id +
            ", status=" + status +
            ", cardNumberLastFour=" + cardNumberLastFour +
            ", expiryMonth=" + expiryMonth +
            ", expiryYear=" + expiryYear +
            ", currency='" + currency + '\'' +
            ", amount=" + amount +
            '}';
  }
}
