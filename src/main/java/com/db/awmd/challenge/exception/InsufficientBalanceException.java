package com.db.awmd.challenge.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

  private static final String ERROR_MSG_INSUFFICIENTBALANCE = "Transfer amount greater than available balance: %s";

    public static String getErrorMessage(BigDecimal amount) {
        return String.format(ERROR_MSG_INSUFFICIENTBALANCE, amount);
    }
  
  public InsufficientBalanceException(BigDecimal amount) {
    super(getErrorMessage(amount));
  }
}
