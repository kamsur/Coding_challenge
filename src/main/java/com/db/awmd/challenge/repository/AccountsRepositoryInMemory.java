package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {
  private static final AccountsRepositoryInMemory accountsRepositoryInMemory=new AccountsRepositoryInMemory();  
  private AccountsRepositoryInMemory(){}  
   
  public static AccountsRepositoryInMemory getAccountsRepositoryInMemory(){  
   return accountsRepositoryInMemory;  
  }  

  private static final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(account.getAccountId());
    }
  }
  @Override
  public void updateAccount(Account account) {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
    	accounts.replace(previousAccount.getAccountId(), account);
    }
  }

  @Override
  public void withDraw(String accountId, BigDecimal amount) throws InsufficientBalanceException{
    try{
    accounts.compute(accountId, (ID,account) -> {account.withDraw(amount);
    return account;}
    );
    }
    catch (InsufficientBalanceException ibe) {
      throw ibe;
    }
  }

  @Override
  public void credit(String accountId, BigDecimal amount) {
    accounts.compute(accountId, (ID,account) -> {account.credit(amount);
      return account;}
      );
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

}
