package com.checkout.payment.gateway.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.ErrorResponse;
import com.checkout.payment.gateway.controller.response.PaymentResponse;
import com.checkout.payment.gateway.enums.ErrorStatus;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

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
    final PostPaymentRequest paymentRequest = createPaymentRequest("123456789101113");
    final String clientResponse = "{\"authorized\": true, \"authorization_code\": \"3c915b87-a3dc-4d3e-b2f7-2b5435f042b4\"}";

    stubBankClient(HttpStatus.OK, clientResponse);

    final var paymentResponse = performPostRequest(objectMapper.writeValueAsString(paymentRequest),
        status().isCreated());

    final var payment = objectMapper.readValue(paymentResponse, PaymentResponse.class);
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
    final PostPaymentRequest paymentRequest = createPaymentRequest("123456789101112");
    final String clientResponse = "{\"authorized\": false}";

    stubBankClient(HttpStatus.OK, clientResponse);

    final var paymentResponse = performPostRequest(objectMapper.writeValueAsString(paymentRequest),
        status().isCreated());

    final var payment = objectMapper.readValue(paymentResponse, PaymentResponse.class);
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
    final var response = performPostRequest(paymentRequest, status().isUnprocessableEntity());

    final var errorResponse = objectMapper.readValue(response, ErrorResponse.class);

    assertAll(
        () -> assertEquals(ErrorStatus.REJECTED, errorResponse.errorStatus()),
        () -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), errorResponse.errorCode()),
        () -> assertEquals("Request rejected", errorResponse.message())
    );
  }

  @Test
  @SneakyThrows
  @DisplayName("Should return BAD_GATEWAY when call to bank client fails")
  void shouldReturnBadGatewayWhenBankClientFails() {
    final PostPaymentRequest paymentRequest = createPaymentRequest("123456789101110");

    stubBankClient(HttpStatus.SERVICE_UNAVAILABLE, null);

    final var response = performPostRequest(objectMapper.writeValueAsString(paymentRequest),
        status().isBadGateway());

    final var errorResponse = objectMapper.readValue(response, ErrorResponse.class);
    assertAll(
        () -> assertEquals(ErrorStatus.BANK_UNAVAILABLE, errorResponse.errorStatus()),
        () -> assertEquals(HttpStatus.BAD_GATEWAY.value(), errorResponse.errorCode()),
        () -> assertEquals("Transaction failed", errorResponse.message())
    );
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
        Arguments.of(
            objMapper.writeValueAsString(paymentRequest.toBuilder().cardNumber("123").build())),
        Arguments.of(incorrectExpiryDate),
        Arguments.of(
            objMapper.writeValueAsString(paymentRequest.toBuilder().expiryYear(2024).build())),
        Arguments.of(
            objMapper.writeValueAsString(paymentRequest.toBuilder().currency("AUD").build())),
        Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cvv("12").build())),
        Arguments.of(objMapper.writeValueAsString(paymentRequest.toBuilder().cvv("12345").build())),
        Arguments.of(objMapper.writeValueAsString(
            paymentRequest.toBuilder().cardNumber("123").currency("AUD").cvv("12").build()))
    );
  }
}
