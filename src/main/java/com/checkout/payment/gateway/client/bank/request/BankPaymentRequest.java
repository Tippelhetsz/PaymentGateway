package com.checkout.payment.gateway.client.bank.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BankPaymentRequest(
    @JsonProperty("card_number")
    String cardNumber,
    @JsonProperty("expiry_date")
    String expiryDate,
    String currency,
    int amount,
    String cvv
) {

}
