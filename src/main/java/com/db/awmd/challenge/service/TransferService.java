package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.*;
import com.db.awmd.challenge.repository.TransfersRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class TransferService {

  @Getter
  private final TransfersRepository transfersRepository;

  @Autowired
  public TransferService(TransfersRepository transfersRepository) {
    this.transfersRepository = transfersRepository;
  }

  public void createTransfer(Transfer transfer) {
	  this.transfersRepository.createTransfer(transfer);
  }

  public Account getAccount(String accountId) {
    return this.transfersRepository.getAccount(accountId);
  }
  public void updateAccount(Account account) {
    this.transfersRepository.updateAccount(account);
  }
  public void withDraw(String accountId, BigDecimal amount) {
    this.transfersRepository.withDraw(accountId,amount);
  }
  public void credit(String accountId, BigDecimal amount) {
    this.transfersRepository.credit(accountId,amount);
  }
  public void notifyTransfer(Account accountFrom, Account accountTo, BigDecimal amount) {
	this.transfersRepository.notifyTransfer(accountFrom,accountTo, amount);
  }
}
