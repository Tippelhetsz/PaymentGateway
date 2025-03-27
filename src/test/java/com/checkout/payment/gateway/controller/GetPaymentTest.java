package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.controller.response.PostPaymentResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetPaymentTest extends PaymentGatewayControllerTest {

    @Test
    void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
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
    void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/v1/payment/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorStatus").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.message").value("Invalid ID"));
    }
}
