package com.checkout.payment.gateway.model.dto;

import com.checkout.payment.gateway.enums.PaymentState;
import com.checkout.payment.gateway.enums.PaymentStatus;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PaymentDto {

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
