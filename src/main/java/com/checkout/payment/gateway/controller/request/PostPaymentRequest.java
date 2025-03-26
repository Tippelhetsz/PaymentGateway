package com.checkout.payment.gateway.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class PostPaymentRequest implements Serializable {

  @NotBlank(message = "Card number is required")
  @Pattern(regexp = "^\\d{14,19}$", message = "Card number must be between 14-19 characters long")
  @JsonProperty("card_number")
  private String cardNumber;

  @NotNull(message = "Expiry month is required")
  @Min(value = 1)
  @Max(value = 12)
  @JsonProperty("expiry_month")
  private Integer expiryMonth;

  @NotNull(message = "Expiry year is required")
  @JsonProperty("expiry_year")
  private Integer expiryYear;

  @NotBlank(message = "Currency is required")
  @Pattern(regexp = "^(USD|GBP|EUR)$", message = "Currency must be 3 characters long. Supported currency: USD, GBP, EUR")
  private String currency;

  @NotNull(message = "Amount is required")
  @Min(value = 1, message = "Amount must be a positive number")
  private Integer amount;

  @NotBlank(message = "CVV is required")
  @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3-4 characters long. Must only contain numeric characters")
  private String cvv;

  @JsonProperty("expiry_date")
  @Future(message = "Expiry date must be in the future")
  public LocalDate getExpiryDate() {
    return LocalDate.of(this.expiryYear, this.expiryMonth, 1);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
