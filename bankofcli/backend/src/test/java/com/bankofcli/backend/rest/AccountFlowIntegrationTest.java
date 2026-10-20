package com.bankofcli.backend.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.bankofcli.backend.domain.Account;
import com.bankofcli.backend.repository.AccountRepository;
import com.bankofcli.backend.repository.TransactionRepository;

/**
 * End-to-end tests over the real HTTP layer: controller, service, repository and
 * GlobalExceptionHandler, against an in-memory H2 database.
 *
 * Covers the endpoints that exist today. Register/login and deposit/withdraw are
 * not here because AuthController is empty and BankService.deposit/withdraw have
 * no bodies yet; add tests here as those land.
 */
@SpringBootTest
@DisplayName("Account and transfer flow over HTTP")
class AccountFlowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private Account savedAccount(String userName, String balance) {
        Account account = new Account("Test", "User", userName, "pw");
        account.setBalance(new BigDecimal(balance));
        return accountRepository.save(account);
    }

    private static String newAccountJson(String userName) {
        return "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\","
                + "\"userName\":\"" + userName + "\",\"password\":\"s3cret\"}";
    }

    private static String transferJson(int fromId, int toId, String amount) {
        return "{\"accountId\":" + fromId + ",\"relatedAccountId\":" + toId
                + ",\"amount\":" + amount + "}";
    }

    private static String amountJson(int accountId, String amount) {
        return "{\"accountId\":" + accountId + ",\"amount\":" + amount + "}";
    }

    @Test
    @DisplayName("POST /api/accounts creates an account and persists it")
    void createsAccount() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAccountJson("ada")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ada"))
                .andExpect(jsonPath("$.firstName").value("Ada"));

        assertTrue(accountRepository.findByUserName("ada").isPresent());
    }

    @Test
    @DisplayName("GET /api/accounts finds an existing account by username")
    void fetchesAccountByUserName() throws Exception {
        savedAccount("ada", "0.00");

        mockMvc.perform(get("/api/accounts").param("username", "ada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ada"));
    }

    @Test
    @DisplayName("GET /api/accounts with an unknown userName returns 404 through the exception handler")
    void unknownUserNameReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/accounts").param("username", "does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/transactions/transfer moves money and records one transaction")
    void transferMovesMoneyAndRecordsTransaction() throws Exception {
        Account sender = savedAccount("sender", "500.00");
        Account recipient = savedAccount("recipient", "100.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(sender.getId(), recipient.getId(), "150.00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.accountId").value(sender.getId()));

        BigDecimal senderBalance = accountRepository.findById(sender.getId()).orElseThrow().getBalance();
        BigDecimal recipientBalance = accountRepository.findById(recipient.getId()).orElseThrow().getBalance();

        assertEquals(0, senderBalance.compareTo(new BigDecimal("350.00")),
                "sender should be debited; was " + senderBalance);
        assertEquals(0, recipientBalance.compareTo(new BigDecimal("250.00")),
                "recipient should be credited; was " + recipientBalance);
        assertEquals(1, transactionRepository.findByAccount_Id(sender.getId()).size());
    }

    @Test
    @DisplayName("A transfer larger than the balance returns 400 and rolls back both sides")
    void transferWithInsufficientFundsRollsBack() throws Exception {
        Account sender = savedAccount("poor", "50.00");
        Account recipient = savedAccount("rich", "1000.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(sender.getId(), recipient.getId(), "100.00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        BigDecimal senderBalance = accountRepository.findById(sender.getId()).orElseThrow().getBalance();
        BigDecimal recipientBalance = accountRepository.findById(recipient.getId()).orElseThrow().getBalance();

        assertEquals(0, senderBalance.compareTo(new BigDecimal("50.00")),
                "sender must be untouched after a failed transfer; was " + senderBalance);
        assertEquals(0, recipientBalance.compareTo(new BigDecimal("1000.00")),
                "recipient must be untouched after a failed transfer; was " + recipientBalance);
        assertEquals(0, transactionRepository.count(), "no transaction should be recorded");
    }

    @Test
    @DisplayName("Transferring to the same account returns 400")
    void transferToSameAccountIsRejected() throws Exception {
        Account account = savedAccount("solo", "500.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(account.getId(), account.getId(), "25.00")))
                .andExpect(status().isBadRequest());

        BigDecimal balance = accountRepository.findById(account.getId()).orElseThrow().getBalance();
        assertEquals(0, balance.compareTo(new BigDecimal("500.00")));
    }

    @Test
    @DisplayName("A zero or negative transfer amount returns 400")
    void transferOfZeroIsRejected() throws Exception {
        Account sender = savedAccount("a", "500.00");
        Account recipient = savedAccount("b", "500.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(sender.getId(), recipient.getId(), "0.00")))
                .andExpect(status().isBadRequest());

        assertEquals(0, transactionRepository.count());
    }

    @Test
    @DisplayName("Transferring from an account that does not exist returns 404")
    void transferFromMissingAccountReturnsNotFound() throws Exception {
        Account recipient = savedAccount("recipient", "100.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(999999, recipient.getId(), "10.00")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A transfer to a missing recipient rolls back the sender's debit")
    void transferToMissingRecipientRollsBackTheDebit() throws Exception {
        Account sender = savedAccount("sender", "500.00");

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson(sender.getId(), 999999, "100.00")))
                .andExpect(status().isNotFound());

        BigDecimal senderBalance = accountRepository.findById(sender.getId()).orElseThrow().getBalance();
        assertEquals(0, senderBalance.compareTo(new BigDecimal("500.00")),
                "the sender's debit must be rolled back when the recipient does not exist; was "
                        + senderBalance);
        assertEquals(0, transactionRepository.count(), "no transaction should be recorded");
    }

    @Test
    @DisplayName("POST /api/transactions/deposit increases the balance and records a DEPOSIT")
    void depositIncreasesBalance() throws Exception {
        Account account = savedAccount("depositor", "100.00");

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amountJson(account.getId(), "250.00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"));

        BigDecimal balance = accountRepository.findById(account.getId()).orElseThrow().getBalance();
        assertEquals(0, balance.compareTo(new BigDecimal("350.00")),
                "deposit should credit the account; was " + balance);
        assertEquals(1, transactionRepository.count());
    }

    @Test
    @DisplayName("POST /api/transactions/withdraw decreases the balance")
    void withdrawDecreasesBalance() throws Exception {
        Account account = savedAccount("withdrawer", "500.00");

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amountJson(account.getId(), "200.00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("WITHDRAW"));

        BigDecimal balance = accountRepository.findById(account.getId()).orElseThrow().getBalance();
        assertEquals(0, balance.compareTo(new BigDecimal("300.00")),
                "withdraw must debit the account, not credit it; balance was " + balance);
    }

    @Test
    @DisplayName("Withdrawing more than the balance returns 400 and changes nothing")
    void withdrawBeyondBalanceIsRejected() throws Exception {
        Account account = savedAccount("broke", "50.00");

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(amountJson(account.getId(), "100000.00")))
                .andExpect(status().isBadRequest());

        BigDecimal balance = accountRepository.findById(account.getId()).orElseThrow().getBalance();
        assertEquals(0, balance.compareTo(new BigDecimal("50.00")),
                "an over-withdrawal must leave the balance untouched; was " + balance);
        assertEquals(0, transactionRepository.count(), "no transaction should be recorded");
    }

    // ---------------------------------------------------------------------
    // The one below documents a real defect. Delete the @Disabled when the
    // defect is fixed and the test should go green.
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Account responses do not contain the password")
    void accountResponseDoesNotExposePassword() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAccountJson("ada")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @Disabled("Defect: Account.balance is never initialised, so a new account persists a null "
            + "balance and the first deposit or withdrawal throws NullPointerException. Fix with "
            + "`private BigDecimal balance = BigDecimal.ZERO;` in Account, then remove this @Disabled.")
    @DisplayName("A newly created account starts at a zero balance, not null")
    void newAccountStartsAtZeroBalance() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAccountJson("ada")))
                .andExpect(status().isCreated());

        Account saved = accountRepository.findByUserName("ada").orElseThrow();
        assertNotNull(saved.getBalance(), "a new account must have a balance, not null");
        assertEquals(0, saved.getBalance().compareTo(BigDecimal.ZERO));
    }
}
