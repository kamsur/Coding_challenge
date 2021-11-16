package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.*;
import com.db.awmd.challenge.exception.*;
import java.math.BigDecimal;

public interface TransfersRepository {

  void createTransfer(Transfer transfer) throws RuntimeException;
  void updateAccount(Account account);
  void withDraw(String accountId, BigDecimal amount) throws InsufficientBalanceException;
  void credit(String accountId, BigDecimal amount);
  Account getAccount(String accountId);

  void notifyTransfer(Account accountFrom, Account accountTo, BigDecimal amount);
}
