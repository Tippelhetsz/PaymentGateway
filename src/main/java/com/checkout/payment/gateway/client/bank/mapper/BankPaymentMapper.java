package com.checkout.payment.gateway.client.bank.mapper;

import com.checkout.payment.gateway.client.bank.request.BankPaymentRequest;
import com.checkout.payment.gateway.model.dto.PaymentDto;
import org.springframework.stereotype.Component;

@Component
public class BankPaymentMapper {

  public BankPaymentRequest toRequest(PaymentDto paymentDto) {
    return new BankPaymentRequest(
        paymentDto.getCardNumber(),
        String.format("%d/%d", paymentDto.getExpiryMonth(), paymentDto.getExpiryYear()),
        paymentDto.getCurrency(),
        paymentDto.getAmount(),
        paymentDto.getCvv()
    );
  }

}
