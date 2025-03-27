package com.checkout.payment.gateway.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.checkout.payment.gateway.exception.PaymentNotFoundException;
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

  public PaymentDto getById(UUID id) {
    return paymentMapper.mapToDto(Optional.ofNullable(payments.get(id))
            .orElseThrow(() -> new PaymentNotFoundException("Invalid ID")));
  }

  public List<PaymentDto> getAll() {
    return payments.values().stream().map(paymentMapper::mapToDto).toList();
  }

  public PaymentDto save(PaymentDto payment) {
    final var newPaymentEntity = paymentMapper.mapToEntity(payment);
    payments.put(newPaymentEntity.getId(), newPaymentEntity);

    return paymentMapper.mapToDto(newPaymentEntity);
  }
}
