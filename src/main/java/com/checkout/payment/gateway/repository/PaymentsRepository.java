package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.controller.response.PostPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.dto.PaymentDto;
import com.checkout.payment.gateway.model.entity.PaymentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PaymentsRepository {

  private final HashMap<UUID, PaymentEntity> payments = new HashMap<>();
  private final PaymentMapper paymentMapper;

  public void add(PaymentEntity payment) {
    payments.put(payment.getId(), payment);
  }

  public PaymentDto get(UUID id) {
    return paymentMapper.mapToDto(Optional.ofNullable(payments.get(id))
            .orElseThrow(() -> new EventProcessingException("Invalid ID")));
  }

  public PaymentDto save(PaymentDto payment) {
    final var newPaymentEntity = paymentMapper.mapToEntity(payment);
    payments.put(newPaymentEntity.getId(), newPaymentEntity);

    return paymentMapper.mapToDto(newPaymentEntity);
  }
}
