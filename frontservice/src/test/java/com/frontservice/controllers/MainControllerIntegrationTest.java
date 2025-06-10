package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.configs.TestSecurityConfig;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.ExchangeRate;
import com.frontservice.dto.RatesDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MainController.class)
@Import({TestSecurityConfig.class, MainControllerIntegrationTest.TestConfig.class})
class MainControllerIntegrationTest {

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
        public ExchangeClient exchangeClient() {
            return mock(ExchangeClient.class);
        }

        @Bean
        public BankService bankService() {
            return mock(BankService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountsClient accountsClient;

    @Autowired
    private ExchangeClient exchangeClient;

    @Autowired
    private BankService bankService;

    private UserInfoDto testUserDto;
    private AccountInfoDto testAccountDto;
    private UserAccountsDto testUserAccountsDto;
    private CurrenciesDto testCurrenciesDto;
    private RatesDto testRatesDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserInfoDto(
            "testuser",
            "Test User",
            LocalDate.now().minusYears(20),
            "test@example.com"
        );

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

        testRatesDto = new RatesDto(List.of(
            new ExchangeRate("RUB", "USD", BigDecimal.valueOf(0.01)),
            new ExchangeRate("RUB", "EUR", BigDecimal.valueOf(0.009))
        ));
    }

    @Test
    @WithMockUser(username = "testuser")
    void showMainPage_ShouldDisplayUserInfoAndAccounts() throws Exception {
        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
        when(accountsClient.getUserInfoDto("testuser")).thenReturn(testUserDto);
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(testUserAccountsDto);
        when(bankService.convertToAccountUserInfoList(any())).thenReturn(List.of());
        when(bankService.combineCurrencies(any(), any())).thenReturn(List.of(testAccountDto));
        when(bankService.convertAllUsersToAccountInfo(any())).thenReturn(List.of());
        when(exchangeClient.getCurrenciesDto()).thenReturn(testCurrenciesDto);

        mockMvc.perform(get("/main"))
            .andExpect(status().isOk())
            .andExpect(view().name("main"))
            .andExpect(model().attribute("login", testUserDto.login()))
            .andExpect(model().attribute("name", testUserDto.name()))
            .andExpect(model().attribute("birthdate", testUserDto.birthdate()))
            .andExpect(model().attribute("email", testUserDto.email()))
            .andExpect(model().attribute("ratesEndpoint", "/api/rates"))
            .andExpect(model().attribute("accounts", List.of(testAccountDto)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getExchangeRates_ShouldReturnRates() throws Exception {
        when(exchangeClient.getRates()).thenReturn(testRatesDto);

        mockMvc.perform(get("/api/rates"))
            .andExpect(status().isOk());
    }
}
