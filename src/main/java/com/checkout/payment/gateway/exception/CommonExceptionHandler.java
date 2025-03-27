package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.controller.response.ErrorResponse;
import com.checkout.payment.gateway.enums.ErrorStatus;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);
  private static final String REQUEST_REJECTED = "Request rejected";

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public @ResponseBody ErrorResponse handleException(PaymentNotFoundException ex) {
    LOG.warn("Payment not found by ID. {}", ex.getMessage());
    return buildErrorResponse(
            ErrorStatus.NOT_FOUND,
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            List.of(ex.getMessage()));
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public @ResponseBody ErrorResponse handleInvalidDateException(ValidationException exception) {
    return buildErrorResponse(
            ErrorStatus.REJECTED,
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            REQUEST_REJECTED,
            List.of("Expiry date is required and must be valid"));
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public @ResponseBody ErrorResponse handleInvalidRequestException(MethodArgumentNotValidException exception) {
    List<String> errors = new ArrayList<>();

    exception.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

    return buildErrorResponse(
            ErrorStatus.REJECTED,
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            REQUEST_REJECTED,
            errors);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  public @ResponseBody ErrorResponse handleBankClientException(BankClientException exception) {
    LOG.error("Bank transaction failed. {}", exception.getMessage());
    return buildErrorResponse(
            ErrorStatus.BANK_UNAVAILABLE,
            HttpStatus.BAD_GATEWAY.value(),
            exception.getMessage(),
            List.of("Bank transaction failed"));
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody ErrorResponse handleConnectionException(ConnectException exception) {
    LOG.error("Connection refused by downstream service");
    return buildErrorResponse(
            ErrorStatus.SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            exception.getMessage(),
            List.of("Something went wrong while processing the request")
    );
  }

  private ErrorResponse buildErrorResponse(ErrorStatus errorStatus, int statusCode, String message, List<String> errors) {
    return new ErrorResponse(errorStatus, statusCode, message, errors);
  }
}
