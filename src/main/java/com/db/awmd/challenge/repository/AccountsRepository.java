package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.*;
import java.math.BigDecimal;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;
  void updateAccount(Account account);
  void withDraw(String accountId, BigDecimal amount) throws InsufficientBalanceException;
  void credit(String accountId, BigDecimal amount);

  Account getAccount(String accountId);

  void clearAccounts();
}
