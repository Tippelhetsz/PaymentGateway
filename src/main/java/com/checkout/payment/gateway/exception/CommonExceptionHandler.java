package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.controller.response.ErrorResponse;
import com.checkout.payment.gateway.enums.ErrorStatus;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public @ResponseBody ErrorResponse handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
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
            "Request rejected",
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
            "Request rejected",
            errors);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  public @ResponseBody ErrorResponse handleBankClientException(BankClientException exception) {
    return buildErrorResponse(
            ErrorStatus.BANK_UNAVAILABLE,
            HttpStatus.BAD_GATEWAY.value(),
            exception.getMessage(),
            List.of("Bank transaction failed"));
  }

  private ErrorResponse buildErrorResponse(ErrorStatus errorStatus, int statusCode, String message, List<String> errors) {
    return new ErrorResponse(errorStatus, statusCode, message, errors);
  }
}
