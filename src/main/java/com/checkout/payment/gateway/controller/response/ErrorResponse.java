package com.checkout.payment.gateway.controller.response;

import com.checkout.payment.gateway.enums.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private final ErrorStatus errorStatus;
  private final int errorCode;
  private final String message;
  private final List<String> errors;

  @Override
  public String toString() {
    return "ErrorResponse{" +
            "status='" + errorStatus + '\'' +
            "code='" + errorCode + '\'' +
            "message='" + message + '\'' +
            "errors='" + errors + '\'' +
            '}';
  }
}
