package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.CashClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.clients.TransferClient;
import com.frontservice.configs.TestSecurityConfig;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CashRequestDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.TransferRequestDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserUpdateDto;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BankController.class)
@Import({TestSecurityConfig.class, BankControllerIntegrationTest.TestConfig.class})
class BankControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        public AccountsClient accountsClient() {
            return mock(AccountsClient.class);
        }

        @Bean
        public TransferClient transferClient() {
            return mock(TransferClient.class);
        }

        @Bean
        public ExchangeClient exchangeClient() {
            return mock(ExchangeClient.class);
        }

        @Bean
        public CashClient cashClient() {
            return mock(CashClient.class);
        }

        @Bean
        public BankService bankService() {
            return mock(BankService.class);
        }

        @Bean
        public MeterRegistry meterRegistry() {
            return mock(MeterRegistry.class);
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountsClient accountsClient;

    @Autowired
    private TransferClient transferClient;

    @Autowired
    private ExchangeClient exchangeClient;

    @Autowired
    private CashClient cashClient;

    @Autowired
    private BankService bankService;

    @Autowired
    private MeterRegistry meterRegistry;

    @Mock
    private Counter mockCounter;

    private UserAccountsDto testUserAccountsDto;
    private AccountInfoDto testAccountDto;
    private CurrenciesDto testCurrenciesDto;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.counter(any()))
            .thenReturn(mockCounter);

        lenient().doNothing().when(mockCounter).increment();

        testAccountDto = new AccountInfoDto(
            1L,
            "Test Account",
            "RUB",
            BigDecimal.valueOf(1000),
            true
        );

        testUserAccountsDto = new UserAccountsDto(
            "testuser",
            "Test User",
            "test@example.com",
            List.of(testAccountDto)
        );

        testCurrenciesDto = new CurrenciesDto(Map.of(
            "RUB", "Рубль",
            "USD", "Доллар",
            "EUR", "Евро"
        ));

    }

    @Test
    @WithMockUser(username = "testuser")
    void transferToSelf_WithValidData_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(bankService.findAccountById(eq(testUserAccountsDto), anyLong())).thenReturn(Optional.of(testAccountDto));
        doNothing().when(transferClient).sendTransferRequest(any(TransferRequestDto.class));

        mockMvc.perform(post("/user/transfer-self")
                .param("fromAccount", "1")
                .param("toAccount", "2")
                .param("value", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attribute("transferSuccess", "Успешный перевод"));

        verify(transferClient).sendTransferRequest(any(TransferRequestDto.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void transferToOther_WithValidData_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(bankService.findLoginByAccountId(any(),any())).thenReturn(Optional.of("login"));
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(bankService.findAccountById(eq(testUserAccountsDto), anyLong())).thenReturn(Optional.of(testAccountDto));
        when(accountsClient.getAllUsersInfoExceptCurrentDto("testuser")).thenReturn(new AllUsersInfoExceptCurrentDto(List.of(testUserAccountsDto)));
        when(bankService.findAccountById(anyList(), anyLong())).thenReturn(Optional.of(testAccountDto));
        doNothing().when(transferClient).sendTransferRequest(any(TransferRequestDto.class));

        mockMvc.perform(post("/user/transfer-other")
                .param("fromAccount", "1")
                .param("toAccount", "2")
                .param("value", "100"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attribute("transferOtherSuccess", "Успешный перевод"));

    }

    @Test
    @WithMockUser(username = "testuser")
    void editUser_WithClientError_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .when(accountsClient).sendUserUpdateRequest(any(UserUpdateDto.class));

        mockMvc.perform(post("/user/edit-user")
                .param("name", "New Name")
                .param("birthdate", LocalDate.now().minusYears(20).toString())
                .param("email", "new@example.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attributeExists("errorUsers"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void editAccounts_WithValidData_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(exchangeClient.getCurrenciesDto()).thenReturn(testCurrenciesDto);
        when(bankService.combineCurrencies(any(), any())).thenReturn(List.of(testAccountDto));
        doNothing().when(accountsClient).sendAccountsUpdateRequest(anyString(), anyList());

        mockMvc.perform(post("/user/edit-accounts")
                .param("account", "RUB", "USD"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attribute("successUpdatedAcc", "Данные обновлены"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void editAccounts_WithClientError_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(exchangeClient.getCurrenciesDto()).thenReturn(testCurrenciesDto);
        when(bankService.combineCurrencies(any(), any())).thenReturn(List.of(testAccountDto));
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .when(accountsClient).sendAccountsUpdateRequest(anyString(), anyList());

        mockMvc.perform(post("/user/edit-accounts")
                .param("account", "RUB", "USD"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attributeExists("userAccountsErrors"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void processCash_WithValidData_ShouldRedirectToMain() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(bankService.findAccountById(eq(testUserAccountsDto), anyLong())).thenReturn(Optional.of(testAccountDto));
        doNothing().when(cashClient).sendCashRequest(any(CashRequestDto.class));

        mockMvc.perform(post("/user/cash")
                .param("accountId", "1")
                .param("value", "100")
                .param("action", "PUT"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main"))
            .andExpect(flash().attribute("cashSuccess", "Операция успешна"));

        verify(cashClient).sendCashRequest(any(CashRequestDto.class));
    }
} 