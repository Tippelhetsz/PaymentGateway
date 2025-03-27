package com.checkout.payment.gateway.model.entity;

import com.checkout.payment.gateway.enums.PaymentState;
import com.checkout.payment.gateway.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class PaymentEntity {

  private UUID id;
  private PaymentStatus status;
  private PaymentState paymentState;
  private String cardNumber;
  private int expiryMonth;
  private int expiryYear;
  private String currency;
  private int amount;
  private String cvv;
  private UUID authorizationCode;

}
