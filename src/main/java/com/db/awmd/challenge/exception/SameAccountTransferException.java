package com.db.awmd.challenge.exception;

public class SameAccountTransferException extends RuntimeException {

  private static final String ERROR_MSG_SAMEACCOUNTTRANSFER = "Transfer initiated to same account as sender: %s";

    public static String getErrorMessage(String accountId) {
        return String.format(ERROR_MSG_SAMEACCOUNTTRANSFER, accountId);
    }
  
  public SameAccountTransferException(String accountId) {
    super(getErrorMessage(accountId));
  }
}
