package com.checkout.payment.gateway.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorStatus {
  REJECTED("Rejected"),
  NOT_FOUND("Not found"),
  BANK_UNAVAILABLE("Bank unavailable"),
  SERVER_ERROR("Server error");

  private final String name;
}
