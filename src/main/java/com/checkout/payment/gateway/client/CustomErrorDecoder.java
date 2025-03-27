package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.exception.BankClientException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    if (response.status() >= 500) {
      return new BankClientException("Transaction failed");
    }

    return defaultErrorDecoder.decode(methodKey, response);
  }
}
