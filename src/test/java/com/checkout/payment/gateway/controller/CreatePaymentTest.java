package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CreatePaymentTest extends PaymentGatewayControllerTest {

    @Test
    @SneakyThrows
    @DisplayName("Should successfully create authorized payment")
    void shouldSuccessfullyCreateAuthorizedPayment() {
        final PostPaymentRequest paymentRequest = PostPaymentRequest.builder()
                .cardNumberLastFour(1235)
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv(123)
                .build();

        final var paymentResponse = mvc.perform(post("/v1/payment")
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final var payment = objectMapper.readValue(paymentResponse, PostPaymentResponse.class);
        assertAll(
                () -> assertEquals(1235, payment.cardNumberLastFour()),
                () -> assertEquals(paymentRequest.getExpiryMonth(), payment.expiryMonth()),
                () -> assertEquals(paymentRequest.getExpiryYear(), payment.expiryYear()),
                () -> assertEquals(paymentRequest.getCurrency(), payment.currency()),
                () -> assertEquals(paymentRequest.getAmount(), payment.amount()),
                () -> assertEquals(PaymentStatus.AUTHORIZED, payment.status())
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("Should successfully create unauthorized payment")
    void shouldSuccessfullyCreateUnauthorizedPayment() {
        final PostPaymentRequest paymentRequest = PostPaymentRequest.builder()
                .cardNumberLastFour(1234)
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv(123)
                .build();

        final var paymentResponse = mvc.perform(post("/v1/payment")
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final var payment = objectMapper.readValue(paymentResponse, PostPaymentResponse.class);
        assertAll(
                () -> assertEquals(1234, payment.cardNumberLastFour()),
                () -> assertEquals(paymentRequest.getExpiryMonth(), payment.expiryMonth()),
                () -> assertEquals(paymentRequest.getExpiryYear(), payment.expiryYear()),
                () -> assertEquals(paymentRequest.getCurrency(), payment.currency()),
                () -> assertEquals(paymentRequest.getAmount(), payment.amount()),
                () -> assertEquals(PaymentStatus.DECLINED, payment.status())
        );
    }

    @ParameterizedTest
    @MethodSource("invalidPaymentRequests")
    @SneakyThrows
    @DisplayName("Should return rejected response when request is invalid")
    void shouldReturnRejectedResponseWhenRequestIsInvalid(PostPaymentRequest paymentRequest) {
        mvc.perform(post("/v1/payment")
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    private static Stream<Arguments> invalidPaymentRequests() {
        PostPaymentRequest paymentRequest = PostPaymentRequest.builder()
                .cardNumberLastFour(1234)
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv(123)
                .build();

        return Stream.of(
                Arguments.of(paymentRequest.toBuilder().cardNumberLastFour(123).build()),
                Arguments.of(paymentRequest.toBuilder().expiryMonth(13).build()),
                Arguments.of(paymentRequest.toBuilder().expiryYear(2024).build()),
                Arguments.of(paymentRequest.toBuilder().currency("AUD").build()),
                Arguments.of(paymentRequest.toBuilder().cvv(12).build()),
                Arguments.of(paymentRequest.toBuilder().cvv(12345).build())
        );
    }
}
