package com.bankofcli.backend.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.bankofcli.backend.repository.AccountRepository;
import com.bankofcli.backend.repository.TransactionRepository;

/**
 * Tests the auth endpoints with the Spring Security filter chain active.
 *
 * AccountFlowIntegrationTest builds MockMvc with webAppContextSetup(...).build(),
 * which does NOT install the security filters, so those tests bypass security
 * entirely. This class applies springSecurity() so the filter chain runs, which
 * is what makes a 401 assertion meaningful.
 */
@SpringBootTest
@DisplayName("Registration and authentication over HTTP")
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    /** CSRF is enabled, so every POST needs .with(csrf()) or it is rejected. */
    private static String registerJson(String username) {
        return "{\"firstName\":\"Ada\",\"lastName\":\"Lovelace\","
                + "\"username\":\"" + username + "\",\"password\":\"s3cretpw\"}";
    }

    @Test
    @DisplayName("POST /api/auth/register creates the account and returns 201")
    void registerCreatesAccount() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("ada")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertTrue(accountRepository.findByUsername("ada").isPresent(),
                "register should persist the account");
    }

    @Test
    @DisplayName("Registering a username that already exists returns 409")
    void registerRejectsDuplicateUsername() throws Exception {
        // first registration succeeds
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("ada")))
                .andExpect(status().isCreated());

        // the second one hits the existsByUsername check in AuthService.register,
        // which throws ResponseStatusException(CONFLICT)
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("ada")))
                .andExpect(status().isConflict());

        assertEquals(1, accountRepository.count(), "the duplicate must not be persisted");
    }

    @Test
    @DisplayName("Registering with a blank username returns 400 from @Valid")
    void registerRejectsBlankUsername() throws Exception {
        // @NotBlank on AccountWriteDTO.username rejects this, but ONLY because
        // the controller parameter is annotated @Valid. Remove @Valid and this
        // test fails with a 201 and a blank username in the database.
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("")))
                .andExpect(status().isBadRequest());

        assertEquals(0, accountRepository.count(), "nothing should be persisted");
    }

    @Test
    @DisplayName("GET /api/auth/me without authentication returns 401")
    void meWithoutAuthenticationIsUnauthorized() throws Exception {
        // No csrf() needed: CSRF only applies to state-changing methods.
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Logging in with the wrong password returns 401")
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson("ada")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"ada\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/accounts requires authentication")
    void accountEndpointRequiresAuthentication() throws Exception {
        // SecurityConfig permits only /api/auth/register, /login, /logout and
        // /csrf, so everything else falls under anyRequest().authenticated().
        mockMvc.perform(get("/api/accounts").param("username", "ada"))
                .andExpect(status().isUnauthorized());
    }
}
