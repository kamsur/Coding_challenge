package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    this.accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = this.accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
      .andExpect(status().isOk())
      .andExpect(
        content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }

  @Test
  public void makeTransfer() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\",\"amount\":1000}")).andExpect(status().isCreated());
    Account account = this.accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("0");
    account = this.accountsService.getAccount("Id-456");
    assertThat(account.getAccountId()).isEqualTo("Id-456");
    assertThat(account.getBalance()).isEqualByComparingTo("2000");
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-456\",\"accountTo\":\"Id-123\",\"amount\":1000}")).andExpect(status().isCreated());
    account = this.accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
    account = this.accountsService.getAccount("Id-456");
    assertThat(account.getAccountId()).isEqualTo("Id-456");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void makeTransferAccountNotFound() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-ABC\",\"accountTo\":\"Id-456\",\"amount\":1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-DEF\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferInsufficientBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\",\"amount\":1001}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-456\",\"accountTo\":\"Id-123\",\"amount\":3000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferNegativeAmount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\",\"amount\":-1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-456\",\"accountTo\":\"Id-123\",\"amount\":-1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferSameAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-456\",\"accountTo\":\"Id-456\",\"amount\":1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-123\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferNoAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountTo\":\"Id-456\",\"amount\":1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"amount\":1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"amount\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferNoAmount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferEmptyAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"\",\"accountTo\":\"Id-456\",\"amount\":1000}")).andExpect(status().isBadRequest());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"\",\"amount\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeTransferEmptyAmount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\",\"amount\":}")).andExpect(status().isBadRequest());
  }

  @Test
  public void makeSameTransferConcurrently() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-123\",\"balance\":2000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountId\":\"Id-456\",\"balance\":0}")).andExpect(status().isCreated());
        class testRunnable implements Runnable{
        private MockMvc mockMvc;
        public testRunnable(MockMvc mockMvc) {
                this.mockMvc=mockMvc;
        } 
        public void run(){  
                try{
                        this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountFrom\":\"Id-123\",\"accountTo\":\"Id-456\",\"amount\":1000}"));
                }  catch(Exception e){}
        }  
        }
    Thread tA=new Thread(new testRunnable(this.mockMvc));  
    Thread tB=new Thread(new testRunnable(this.mockMvc));
    Thread tC=new Thread(new testRunnable(this.mockMvc));
    tA.start();  
    tB.start();
    tC.start();
    while(tA.isAlive()||tB.isAlive()||tC.isAlive())
    ;  
    Account account = this.accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("0");
    account = this.accountsService.getAccount("Id-456");
    assertThat(account.getAccountId()).isEqualTo("Id-456");
    assertThat(account.getBalance()).isEqualByComparingTo("2000");
  }

  @Test
  public void makeDifferentTransferConcurrently() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-456\",\"balance\":0}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-789\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-ABC\",\"balance\":1000}")).andExpect(status().isCreated());
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-DEF\",\"balance\":1000}")).andExpect(status().isCreated());
    class testRunnable implements Runnable{
    private MockMvc mockMvc;
    private String accountFrom;
    private String accountTo;
    private BigDecimal amount;
        public testRunnable(MockMvc mockMvc, String accountFrom, String accountTo, BigDecimal amount) {
            this.mockMvc=mockMvc;
            this.accountFrom=accountFrom;
            this.accountTo=accountTo;
            this.amount=amount;
        } 
        public void run(){  
            try{
                    this.mockMvc.perform(post("/transfer").contentType(MediaType.APPLICATION_JSON)
            .content("{\"accountFrom\":\""+this.accountFrom+"\",\"accountTo\":\""+this.accountTo+"\",\"amount\":"+this.amount+"}"));
            }  catch(Exception e){}
        }  
    }
        Thread tA=new Thread(new testRunnable(this.mockMvc,"Id-123","Id-456",new BigDecimal(1000)));  
        Thread tB=new Thread(new testRunnable(this.mockMvc,"Id-456","Id-789",new BigDecimal(1000)));
        Thread tC=new Thread(new testRunnable(this.mockMvc,"Id-ABC","Id-DEF",new BigDecimal(1000)));
        tA.start();  
        tB.start();
        tC.start();
        while(tA.isAlive()||tB.isAlive()||tC.isAlive())
        ;  
        Account account = this.accountsService.getAccount("Id-123");
        assertThat(account.getAccountId()).isEqualTo("Id-123");
        assertThat(account.getBalance()).isEqualByComparingTo("0");
        account = this.accountsService.getAccount("Id-456");
        assertThat(account.getAccountId()).isEqualTo("Id-456");
        if(account.getBalance().equals(new BigDecimal(0)))
        {
        account = this.accountsService.getAccount("Id-789");
        assertThat(account.getAccountId()).isEqualTo("Id-789");
        assertThat(account.getBalance()).isEqualByComparingTo("2000");
        }
        else if(account.getBalance().equals(new BigDecimal(1000)))
        {
        account = this.accountsService.getAccount("Id-789");
        assertThat(account.getAccountId()).isEqualTo("Id-789");
        assertThat(account.getBalance()).isEqualByComparingTo("1000");
        }
        else{
        fail("Corrupt transfer occured");
        }
        account = this.accountsService.getAccount("Id-ABC");
        assertThat(account.getAccountId()).isEqualTo("Id-ABC");
        assertThat(account.getBalance()).isEqualByComparingTo("0");
        account = this.accountsService.getAccount("Id-DEF");
        assertThat(account.getAccountId()).isEqualTo("Id-DEF");
        assertThat(account.getBalance()).isEqualByComparingTo("2000");
   }

}
