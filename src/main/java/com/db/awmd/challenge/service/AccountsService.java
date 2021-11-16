package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }
  public void updateAccount(Account account){this.accountsRepository.updateAccount(account);}
  public void withDraw(String accountId, BigDecimal amount) {
    this.accountsRepository.withDraw(accountId,amount);
  }
  public void credit(String accountId, BigDecimal amount) {
    this.accountsRepository.credit(accountId,amount);
  }
  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
}
