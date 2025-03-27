package com.checkout.payment.gateway.controller.response;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"id", "status", "card_number_last_four", "expiry_month", "expiry_year", "currency", "amount"})
public record PaymentResponse(
        UUID id,
        PaymentStatus status,
        @JsonProperty("card_number_last_four") int cardNumberLastFour,
        @JsonProperty("expiry_month") int expiryMonth,
        @JsonProperty("expiry_year") int expiryYear,
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
