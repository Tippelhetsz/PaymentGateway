package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PostPaymentResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WireMockTest(httpPort = 8081)
public class CreatePaymentTest extends PaymentGatewayControllerTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("bankclient.url", () -> "http://localhost:8081");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should successfully create authorized payment")
    void shouldSuccessfullyCreateAuthorizedPayment() {
        final PostPaymentRequest paymentRequest = PostPaymentRequest.builder()
                .cardNumber("123456789101113")
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv("123")
                .build();

        stubFor(WireMock.post(urlMatching("/payments")).
                willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"authorized\": true, \"authorization_code\": \"3c915b87-a3dc-4d3e-b2f7-2b5435f042b4\"}")
                ));

        final var paymentResponse = mvc.perform(post("/v1/payment")
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final var payment = objectMapper.readValue(paymentResponse, PostPaymentResponse.class);
        assertAll(
                () -> assertEquals(1113, payment.cardNumberLastFour()),
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
                .cardNumber("123456789101112")
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv("123")
                .build();

        stubFor(WireMock.post(urlMatching("/payments")).
                willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"authorized\": false}")
                ));

        final var paymentResponse = mvc.perform(post("/v1/payment")
                        .content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final var payment = objectMapper.readValue(paymentResponse, PostPaymentResponse.class);
        assertAll(
                () -> assertEquals(1112, payment.cardNumberLastFour()),
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
    void shouldReturnRejectedResponseWhenRequestIsInvalid(String paymentRequest) {
        mvc.perform(post("/v1/payment")
                        .content(paymentRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    private static Stream<Arguments> invalidPaymentRequests() {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        final String incorrectExpiryDate = "{\"currency\":\"USD\",\"amount\":123,\"cvv\":\"123\",\"card_number\":\"123\",\"expiry_month\":13,\"expiry_year\":2025,\"expiry_date\":\"2025-13-01\"}";

        PostPaymentRequest paymentRequest = PostPaymentRequest.builder()
                .cardNumber("123456789101113")
                .expiryMonth(12)
                .expiryYear(2025)
                .currency("USD")
                .amount(123)
                .cvv("123")
                .build();

        return Stream.of(
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cardNumber("123").build())),
                Arguments.of(incorrectExpiryDate),
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().expiryYear(2024).build())),
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().currency("AUD").build())),
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cvv("12").build())),
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cvv("12345").build())),
                Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cardNumber("123").currency("AUD").cvv("12").build()))
        );
    }
}
