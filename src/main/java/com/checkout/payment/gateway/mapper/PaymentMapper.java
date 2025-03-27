package com.checkout.payment.gateway.mapper;

import com.checkout.payment.gateway.enums.PaymentState;
import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PaymentResponse;
import com.checkout.payment.gateway.model.dto.PaymentDto;
import com.checkout.payment.gateway.model.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMapper {

  public PaymentDto mapToDto(PostPaymentRequest paymentRequest) {
    return PaymentDto.builder()
        .id(UUID.randomUUID())
        .cardNumber(paymentRequest.getCardNumber())
        .paymentState(PaymentState.PENDING)
        .expiryMonth(paymentRequest.getExpiryMonth())
        .expiryYear(paymentRequest.getExpiryYear())
        .currency(paymentRequest.getCurrency())
        .amount(paymentRequest.getAmount())
        .cvv(paymentRequest.getCvv())
        .build();
  }

  public PaymentDto mapToDto(PaymentEntity paymentEntity) {
    return PaymentDto.builder()
        .id(paymentEntity.getId())
        .cardNumber(paymentEntity.getCardNumber())
        .paymentState(paymentEntity.getPaymentState())
        .status(paymentEntity.getStatus())
        .expiryMonth(paymentEntity.getExpiryMonth())
        .expiryYear(paymentEntity.getExpiryYear())
        .currency(paymentEntity.getCurrency())
        .amount(paymentEntity.getAmount())
        .cvv(paymentEntity.getCvv())
        .authorizationCode(paymentEntity.getAuthorizationCode())
        .build();
  }

  public PaymentEntity mapToEntity(PaymentDto paymentDto) {
    return PaymentEntity.builder()
        .id(paymentDto.getId())
        .cardNumber(paymentDto.getCardNumber())
        .paymentState(paymentDto.getPaymentState())
        .status(paymentDto.getStatus())
        .expiryMonth(paymentDto.getExpiryMonth())
        .expiryYear(paymentDto.getExpiryYear())
        .currency(paymentDto.getCurrency())
        .amount(paymentDto.getAmount())
        .cvv(paymentDto.getCvv())
        .authorizationCode(paymentDto.getAuthorizationCode())
        .build();
  }

  public PaymentResponse mapToPaymentResponse(PaymentDto paymentDto) {
    return new PaymentResponse(
        paymentDto.getId(),
        paymentDto.getStatus(),
        Integer.parseInt(
            paymentDto.getCardNumber().substring(paymentDto.getCardNumber().length() - 4)),
        paymentDto.getExpiryMonth(),
        paymentDto.getExpiryYear(),
        paymentDto.getCurrency(),
        paymentDto.getAmount()
    );
  }

}
