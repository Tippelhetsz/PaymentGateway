package com.checkout.payment.gateway.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number_last_four")
  private int cardNumberLastFour;
  @JsonProperty("expiry_month")
  private int expiryMonth;
  @JsonProperty("expiry_year")
  private int expiryYear;
  private String currency;
  private int amount;
  private int cvv;

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumberLastFour +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
