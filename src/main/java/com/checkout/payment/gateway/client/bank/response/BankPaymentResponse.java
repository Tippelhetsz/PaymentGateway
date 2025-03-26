package com.checkout.payment.gateway.client.bank.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record BankPaymentResponse(
    boolean authorized,
    @JsonProperty("authorization_code") UUID authorizationCode)
{ }
