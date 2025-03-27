package com.checkout.payment.gateway.controller.response;

import com.checkout.payment.gateway.enums.ErrorStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ErrorResponse(
        @JsonProperty("error_status")ErrorStatus errorStatus,
        @JsonProperty("error_code")int errorCode,
        String message,
        List<String> errors) {

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
