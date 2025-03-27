package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.bank.BankClient;
import com.checkout.payment.gateway.mapper.bankservice.BankPaymentMapper;
import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PaymentResponse;
import com.checkout.payment.gateway.enums.PaymentState;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankClientException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.dto.PaymentDto;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final PaymentMapper paymentMapper;
  private final BankClient bankClient;
  private final BankPaymentMapper bankPaymentMapper;

  public PaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentMapper.mapToPaymentResponse(paymentsRepository.getById(id));
  }

  public List<PaymentResponse> getAllPayments() {
    LOG.debug("Fetching all payments");
    return paymentsRepository.getAll().stream().map(paymentMapper::mapToPaymentResponse).toList();
  }

  public PaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    var payment = paymentsRepository.save(paymentMapper.mapToDto(paymentRequest));

    var updatedPayment = sendTransactionAndUpdatePayment(payment);

    return paymentMapper.mapToPaymentResponse(updatedPayment);
  }

  private PaymentDto sendTransactionAndUpdatePayment(PaymentDto payment) {
    try {
      final var bankResponse = bankClient.sendPaymentToBank(bankPaymentMapper.toRequest(payment));

      payment = payment.toBuilder()
              .authorizationCode(bankResponse.authorizationCode())
              .paymentState(PaymentState.COMPLETED)
              .status(bankResponse.authorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED)
              .build();

      return paymentsRepository.save(payment);
    } catch (BankClientException exception) {
      payment = payment.toBuilder()
              .paymentState(PaymentState.FAILED)
              .build();

      paymentsRepository.save(payment);

      throw exception;
    }
  }
}
