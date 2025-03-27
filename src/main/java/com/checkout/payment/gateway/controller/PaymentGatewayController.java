package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
@RequestMapping("/v1/payment")
@AllArgsConstructor
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return ResponseEntity.ok(paymentGatewayService.getPaymentById(id));
  }

  @GetMapping
  public ResponseEntity<List<PaymentResponse>> getAllPostPaymentEvent() {
    return ResponseEntity.ok(paymentGatewayService.getAllPayments());
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> createPostPayment(
      @Valid @RequestBody PostPaymentRequest paymentRequest) {
    final var payment = paymentGatewayService.processPayment(paymentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(payment);
  }
}
