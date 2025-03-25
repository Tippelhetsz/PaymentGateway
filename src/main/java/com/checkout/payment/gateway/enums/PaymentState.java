package com.checkout.payment.gateway.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PaymentState {
  PENDING("Pending"),
  COMPLETED("Completed"),
  FAILED("Failed");

  private final String name;
}
