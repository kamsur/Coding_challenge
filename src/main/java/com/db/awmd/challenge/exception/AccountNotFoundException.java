package com.db.awmd.challenge.exception;

public class AccountNotFoundException extends RuntimeException {
  
  private static final String ERROR_MSG_ACCOUNTNOTFOUND = "Account not found, id : %s";

    public static String getErrorMessage(String accountId) {
        return String.format(ERROR_MSG_ACCOUNTNOTFOUND, accountId);
    }

  public AccountNotFoundException(String accountId) {
        super(getErrorMessage(accountId));
    }
}
