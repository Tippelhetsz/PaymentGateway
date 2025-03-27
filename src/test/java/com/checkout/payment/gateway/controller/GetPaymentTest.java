package com.checkout.payment.gateway.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.controller.response.PaymentResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.entity.PaymentEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class GetPaymentTest extends PaymentGatewayControllerTest {

  @Test
  @SneakyThrows
  @DisplayName("When payment with ID exist then correct payment is returned")
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() {
    final var paymentEntity = PaymentEntity.builder()
        .id(UUID.randomUUID())
        .status(PaymentStatus.AUTHORIZED)
        .cardNumber("12345678904321")
        .expiryMonth(12)
        .expiryYear(2024)
        .currency("USD")
        .amount(10)
        .build();

    paymentsRepository.add(paymentEntity);

    mvc.perform(MockMvcRequestBuilders.get("/v1/payment/" + paymentEntity.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(paymentEntity.getStatus().getName()))
        .andExpect(jsonPath("$.card_number_last_four").value(4321))
        .andExpect(jsonPath("$.expiry_month").value(paymentEntity.getExpiryMonth()))
        .andExpect(jsonPath("$.expiry_year").value(paymentEntity.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(paymentEntity.getCurrency()))
        .andExpect(jsonPath("$.amount").value(paymentEntity.getAmount()));
  }

  @Test
  @SneakyThrows
  @DisplayName("When payment with ID does not exist then NOT_FOUND is returned")
  void whenPaymentWithIdDoesNotExistThen404IsReturned() {
    mvc.perform(MockMvcRequestBuilders.get("/v1/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_status").value("NOT_FOUND"))
        .andExpect(jsonPath("$.error_code").value(404))
        .andExpect(jsonPath("$.message").value("Invalid ID"));
  }

  @Test
  @SneakyThrows
  @DisplayName("Should return all payments")
  void shouldReturnAllPayments() {
    var paymentEntity = PaymentEntity.builder()
        .status(PaymentStatus.AUTHORIZED)
        .cardNumber("12345678904321")
        .expiryMonth(12)
        .expiryYear(2024)
        .currency("USD")
        .amount(10)
        .build();

    for (int i = 1; i <= 5; i++) {
      paymentsRepository.add(paymentEntity.toBuilder().id(UUID.randomUUID()).build());
    }

    final var paymentResponse = mvc.perform(MockMvcRequestBuilders.get("/v1/payment"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    final var paymentList = objectMapper.readValue(paymentResponse,
        new TypeReference<List<PaymentResponse>>() {
        });

    assertEquals(5, paymentList.size());
  }
}
