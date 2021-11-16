package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  @Autowired
  private TransferService transferService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-addAcc");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-addAcc")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void withdrawFromAccount() throws Exception {
    String uniqueId = "Id-wFA";
    Account account = new Account(uniqueId);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    this.accountsService.withDraw(uniqueId, new BigDecimal(1000));
    assertThat(this.accountsService.getAccount(uniqueId).getBalance()).isEqualTo(new BigDecimal(0));
  }

  @Test
  public void withdrawFromAccount_failsOnInsufficientBalance() throws Exception {
    String uniqueId = "Id-wFA_fails";
    Account account = new Account(uniqueId);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    this.accountsService.withDraw(uniqueId, new BigDecimal(1000));
    try{
    this.accountsService.withDraw(uniqueId, new BigDecimal(1000));
    fail("Should have failed when withdrawing more than available balance");
    } catch (InsufficientBalanceException ibe) {
      assertThat(ibe.getMessage()).isEqualTo(String.format("Transfer amount greater than available balance: %s",new BigDecimal(0)));
    }
  }

  @Test
  public void creditToAccount() throws Exception {
    String uniqueId = "Id-cTA";
    Account account = new Account(uniqueId);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    this.accountsService.credit(uniqueId, new BigDecimal(1000));
    assertThat(this.accountsService.getAccount(uniqueId).getBalance()).isEqualTo(new BigDecimal(2000));
  }

  @Test
  public void makeTransfer() throws Exception {
    String uniqueIdFrom = "Id-mTFrom";
    Account account = new Account(uniqueIdFrom);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    String uniqueIdTo = "Id-mTTo";
    account = new Account(uniqueIdTo);
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    Transfer transfer=new Transfer(uniqueIdFrom, uniqueIdTo, new BigDecimal(1000));
    this.transferService.createTransfer(transfer);
    assertThat(this.accountsService.getAccount(uniqueIdFrom).getBalance()).isEqualTo(new BigDecimal(0));
    assertThat(this.accountsService.getAccount(uniqueIdTo).getBalance()).isEqualTo(new BigDecimal(2000));
  }

  @Test
  public void makeSameTransferConcurrently() throws Exception {
    String uniqueIdFrom = "Id-mSTCFrom";
    Account account = new Account(uniqueIdFrom);
    account.setBalance(new BigDecimal(2000));
    this.accountsService.createAccount(account);
    String uniqueIdTo = "Id-mSTCTo";
    account = new Account(uniqueIdTo);
    account.setBalance(new BigDecimal(0));
    this.accountsService.createAccount(account);
    Transfer transfer=new Transfer(uniqueIdFrom, uniqueIdTo, new BigDecimal(1000));
        class testRunnable implements Runnable{
        private Transfer transfer;
        private TransferService transferService;
        public testRunnable(Transfer transfer, TransferService transferService) {
                this.transfer=transfer;
                this.transferService=transferService;
        } 
        public void run(){  
                try{
                  this.transferService.createTransfer(transfer);
    } catch (InsufficientBalanceException ibe) {
      assertThat(ibe.getMessage()).isEqualTo(String.format("Transfer amount greater than available balance: %s",new BigDecimal(0)));
    }
        }  
        }
    Thread tA=new Thread(new testRunnable(transfer,this.transferService));  
    Thread tB=new Thread(new testRunnable(transfer,this.transferService));
    Thread tC=new Thread(new testRunnable(transfer,this.transferService));
    tA.start();  
    tB.start();
    tC.start();
    while(tA.isAlive()||tB.isAlive()||tC.isAlive())
    ;  
    assertThat(this.accountsService.getAccount(uniqueIdFrom).getBalance()).isEqualTo(new BigDecimal(0));
    assertThat(this.accountsService.getAccount(uniqueIdTo).getBalance()).isEqualTo(new BigDecimal(2000));
  }

  @Test
  public void makeDifferentTransferConcurrently() throws Exception {
        String uniqueIdFrom = "Id-mDTC_B";
        Account account = new Account(uniqueIdFrom);
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);
        String uniqueIdTo = "Id-mDTC_A";
        account = new Account(uniqueIdTo);
        account.setBalance(new BigDecimal(0));
        this.accountsService.createAccount(account);
        Transfer transfer=new Transfer(uniqueIdFrom, uniqueIdTo, new BigDecimal(1000));
            class testRunnable implements Runnable{
            private Transfer transfer;
            private TransferService transferService;
            public testRunnable(Transfer transfer, TransferService transferService) {
                    this.transfer=transfer;
                    this.transferService=transferService;
            } 
            public void run(){  
                    try{
                      this.transferService.createTransfer(transfer);
        } catch (InsufficientBalanceException ibe) {
          assertThat(ibe.getMessage()).isEqualTo(String.format("Transfer amount greater than available balance: %s",new BigDecimal(0)));
        }
            }  
            }
        Thread tA=new Thread(new testRunnable(transfer,this.transferService));
        uniqueIdFrom="Id-mDTC_B";
        uniqueIdTo = "Id-mDTC_C";
        account = new Account(uniqueIdTo);
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);
        transfer=new Transfer(uniqueIdFrom, uniqueIdTo, new BigDecimal(1000));  
        Thread tB=new Thread(new testRunnable(transfer,this.transferService));
        uniqueIdFrom = "Id-mDTC_D";
        account = new Account(uniqueIdFrom);
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);
        uniqueIdTo = "Id-mDTC_E";
        account = new Account(uniqueIdTo);
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);
        transfer=new Transfer(uniqueIdFrom, uniqueIdTo, new BigDecimal(1000));
        Thread tC=new Thread(new testRunnable(transfer,this.transferService));
        tA.start();  
        tB.start();
        tC.start();
        while(tA.isAlive()||tB.isAlive()||tC.isAlive())
        ;  
        assertThat(this.accountsService.getAccount("Id-mDTC_B").getBalance()).isEqualTo(new BigDecimal(0));
        if(this.accountsService.getAccount("Id-mDTC_A").getBalance().equals(new BigDecimal(0))){
        assertThat(this.accountsService.getAccount("Id-mDTC_C").getBalance()).isEqualTo(new BigDecimal(2000));
        }
        else if(this.accountsService.getAccount("Id-mDTC_A").getBalance().equals(new BigDecimal(1000))){
        assertThat(this.accountsService.getAccount("Id-mDTC_C").getBalance()).isEqualTo(new BigDecimal(1000));
        }
        else{
          fail("Corrupt transfer occured");
        }
        assertThat(this.accountsService.getAccount("Id-mDTC_D").getBalance()).isEqualTo(new BigDecimal(0));
        assertThat(this.accountsService.getAccount("Id-mDTC_E").getBalance()).isEqualTo(new BigDecimal(2000));
   }

}
