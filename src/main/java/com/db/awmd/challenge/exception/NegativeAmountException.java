package com.db.awmd.challenge.exception;

import java.math.BigDecimal;

public class NegativeAmountException extends RuntimeException {

  private static final String ERROR_MSG_NEGATIVEAMOUNT = "Transfer amount %s must be greater than zero!";

    public static String getErrorMessage(BigDecimal amount) {
        return String.format(ERROR_MSG_NEGATIVEAMOUNT, amount);
    }
  
  public NegativeAmountException(BigDecimal amount) {
    super(getErrorMessage(amount));
  }
}
