package com.checkout.payment.gateway.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  public MockMvc mvc;
  @Autowired
  public PaymentsRepository paymentsRepository;
  @Autowired
  public ObjectMapper objectMapper;

  public PostPaymentRequest createPaymentRequest(String cardNumber) {
    return PostPaymentRequest.builder()
        .cardNumber(cardNumber)
        .expiryMonth(12)
        .expiryYear(2025)
        .currency("USD")
        .amount(123)
        .cvv("123")
        .build();
  }

  public void stubBankClient(HttpStatus status, String response) {
    stubFor(WireMock.post(urlMatching("/payments")).
        willReturn(aResponse()
            .withStatus(status.value())
            .withHeader("Content-Type", "application/json")
            .withBody(response)
        ));
  }

  @SneakyThrows
  public String performPostRequest(String paymentRequest, ResultMatcher resultMatcher) {
    return mvc.perform(post("/v1/payment")
            .content(paymentRequest)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(resultMatcher)
        .andReturn().getResponse().getContentAsString();
  }
}
