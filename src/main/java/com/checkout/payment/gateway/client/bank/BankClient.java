package com.checkout.payment.gateway.client.bank;

import com.checkout.payment.gateway.client.bank.request.BankPaymentRequest;
import com.checkout.payment.gateway.client.bank.response.BankPaymentResponse;
import com.checkout.payment.gateway.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "bankclient", url = "${bankclient.url}", configuration = FeignConfiguration.class)
public interface BankClient {

  @RequestMapping(method = RequestMethod.POST, value = "/payments", consumes = "application/json")
  BankPaymentResponse sendPaymentToBank(BankPaymentRequest paymentRequest);

}
