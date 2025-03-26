package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.bank.BankClient;
import com.checkout.payment.gateway.client.bank.mapper.BankPaymentMapper;
import com.checkout.payment.gateway.enums.PaymentState;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PostPaymentResponse;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final PaymentMapper paymentMapper;
  private final BankClient bankClient;
  private final BankPaymentMapper bankPaymentMapper;

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentMapper.mapToPaymentResponse(paymentsRepository.get(id));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    var payment = paymentsRepository.save(paymentMapper.mapToDto(paymentRequest));

    final var bankResponse = bankClient.sendPaymentToBank(bankPaymentMapper.toRequest(payment));

    payment = payment.toBuilder()
            .authorizationCode(bankResponse.authorizationCode())
            .paymentState(PaymentState.COMPLETED)
            .status(bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED)
            .build();

    var updatedPayment = paymentsRepository.save(payment);

    return paymentMapper.mapToPaymentResponse(updatedPayment);
  }
}
