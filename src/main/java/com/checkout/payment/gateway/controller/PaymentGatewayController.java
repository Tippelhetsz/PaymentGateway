package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.controller.request.PostPaymentRequest;
import com.checkout.payment.gateway.controller.response.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("api")
@RequestMapping("/v1/payment")
@AllArgsConstructor
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  @GetMapping("/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<PostPaymentResponse> createPostPayment(@Valid @RequestBody PostPaymentRequest paymentRequest) {
    final var payment = paymentGatewayService.processPayment(paymentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(payment);
  }
}
