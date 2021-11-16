package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.*;
import com.db.awmd.challenge.exception.*;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.AccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

@Slf4j
@Repository
public class TransfersRepositoryInMemory implements TransfersRepository {
  private static final TransfersRepositoryInMemory transfersRepositoryInMemory=new TransfersRepositoryInMemory();  
  private TransfersRepositoryInMemory(){}  
   
  public static TransfersRepositoryInMemory getTransfersRepositoryInMemory(){  
   return transfersRepositoryInMemory;  
  } 

  @Autowired
  private AccountsService accountsService;

  @Override
  @Synchronized
  public void createTransfer(Transfer transfer) throws RuntimeException {
	  Account accountFrom=this.getAccount(transfer.getAccountFrom());
	  Account accountTo=this.getAccount(transfer.getAccountTo());
	    if (accountFrom== null) {
			log.info("Transfer failed {}", transfer);
	      throw new AccountNotFoundException(transfer.getAccountFrom());
	    }
	    else if (accountTo == null) {
			log.info("Transfer failed {}", transfer);
	      throw new AccountNotFoundException(transfer.getAccountTo());
	    }
	    else if (accountFrom.getAccountId().equals(accountTo.getAccountId())) {
			log.info("Transfer failed {}", transfer);
		      throw new SameAccountTransferException(accountTo.getAccountId());
		}
	    else if (transfer.getAmount().compareTo(BigDecimal.ZERO)<=0 ) {
			log.info("Transfer failed {}", transfer);
	      throw new NegativeAmountException(transfer.getAmount());
	    }
	    else {
			try {
				this.withDraw(accountFrom.getAccountId(),transfer.getAmount());
			} catch (InsufficientBalanceException ibe) {
				log.info("Transfer failed {}", transfer);
				throw ibe;
			}
			this.credit(accountTo.getAccountId(),transfer.getAmount());
			log.info("Transfer complete {}", transfer);
			this.notifyTransfer(accountFrom,accountTo, transfer.getAmount());
		}
  }
  @Override
  public void updateAccount(Account account) {
	this.accountsService.updateAccount(account);
  }

  @Override
  public void withDraw(String accountId, BigDecimal amount) throws InsufficientBalanceException{
    try{
    this.accountsService.withDraw(accountId,amount);
    }
    catch (InsufficientBalanceException ibe) {
      throw ibe;
    }
  }

  @Override
  public void credit(String accountId, BigDecimal amount) {
    this.accountsService.credit(accountId,amount);
  }

  @Override
  public Account getAccount(String accountId) {
    return this.accountsService.getAccount(accountId);
  }

  @Override
  public void notifyTransfer(Account accountFrom, Account accountTo, BigDecimal amount) {
	  EmailNotificationService emailnotificationService=new EmailNotificationService();
	  emailnotificationService.notifyAboutTransfer(accountTo,"You have received "+amount+" from Account: "+accountFrom.getAccountId());
	  emailnotificationService.notifyAboutTransfer(accountFrom,"You have transferred "+amount+" to Account: "+accountTo.getAccountId());
  }

}
