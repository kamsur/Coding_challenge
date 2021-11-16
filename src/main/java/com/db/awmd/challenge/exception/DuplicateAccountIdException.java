package com.db.awmd.challenge.exception;

public class DuplicateAccountIdException extends RuntimeException {
  
  private static final String ERROR_MSG_DUBLICATEACCOUNT = "Account id %s already exists!";

    public static String getErrorMessage(String accountId) {
        return String.format(ERROR_MSG_DUBLICATEACCOUNT, accountId);
    }

 public DuplicateAccountIdException(String accountId) {
        super(getErrorMessage(accountId));
    }
}
