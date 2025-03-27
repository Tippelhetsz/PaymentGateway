package com.checkout.payment.gateway.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorStatus {
  REJECTED("Rejected"),
  NOT_FOUND("Not found"),
  BANK_UNAVAILABLE("Bank unavailable");

  private final String name;
}
