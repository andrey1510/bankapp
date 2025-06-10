package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AccountUserInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.ExchangeRate;
import com.frontservice.dto.RatesDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private ExchangeClient exchangeClient;

    @Mock
    private BankService bankService;

    @Mock
    private Model model;

    @InjectMocks
    private MainController mainController;

    private UserInfoDto userInfo;
    private UserAccountsDto userAccountsDto;
    private AccountInfoDto account1;
    private AccountInfoDto account2;
    private AllUsersInfoExceptCurrentDto allUsersInfo;
    private CurrenciesDto currenciesDto;
    private RatesDto ratesDto;
    private List<AccountUserInfoDto> accountUserInfoList;
    private List<AccountInfoDto> combinedAccounts;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        account1 = new AccountInfoDto(
            1L,
            "Main Account",
            "USD",
            BigDecimal.valueOf(1000),
            true
        );

        account2 = new AccountInfoDto(
            2L,
            "Secondary Account",
            "EUR",
            BigDecimal.valueOf(500),
            true
        );

        userInfo = new UserInfoDto(
            "testuser",
            "Test User",
            LocalDate.now().minusYears(20),
            "test@example.com"
        );

        userAccountsDto = new UserAccountsDto(
            "testuser",
            "Test User",
            "test@example.com",
            List.of(account1, account2)
        );

        allUsersInfo = new AllUsersInfoExceptCurrentDto(
            List.of(new UserAccountsDto(
                "otheruser",
                "Other User",
                "other@example.com",
                List.of(account2)
            ))
        );

        currenciesDto = new CurrenciesDto(
            Map.of(
                "USD", "US Dollar",
                "EUR", "Euro"
            )
        );

        ratesDto = new RatesDto(
            List.of(
                new ExchangeRate("Доллары","USD", BigDecimal.valueOf(1.0)),
                new ExchangeRate("Евро", "EUR", BigDecimal.valueOf(0.85))
            )
        );

        accountUserInfoList = List.of(
            new AccountUserInfoDto(
                "Test User",
                1L,
                "test@example.com",
                "Main Account",
                "USD",
                BigDecimal.valueOf(1000)
            )
        );

        combinedAccounts = List.of(
            new AccountInfoDto(
                1L,
                "Main Account",
                "USD",
                BigDecimal.valueOf(1000),
                true
            )
        );

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void dashboard_WithValidData_ShouldAddAllAttributes() {
        when(accountsClient.getUserInfoDto("testuser")).thenReturn(userInfo);
        when(accountsClient.getUserAccountsDto("testuser")).thenReturn(userAccountsDto);
        when(accountsClient.getAllUsersInfoExceptCurrentDto("testuser")).thenReturn(allUsersInfo);
        when(exchangeClient.getCurrenciesDto()).thenReturn(currenciesDto);
        when(bankService.convertToAccountUserInfoList(any())).thenReturn(accountUserInfoList);
        when(bankService.combineCurrencies(any(), any())).thenReturn(combinedAccounts);
        when(bankService.convertAllUsersToAccountInfo(any())).thenReturn(accountUserInfoList);

        String viewName = mainController.dashboard(model);

        assertEquals("main", viewName);
        verify(model).addAttribute("ratesEndpoint", "/api/rates");
        verify(model).addAttribute("login", userInfo.login());
        verify(model).addAttribute("name", userInfo.name());
        verify(model).addAttribute("birthdate", userInfo.birthdate());
        verify(model).addAttribute("email", userInfo.email());
        verify(model).addAttribute("transferAccounts", accountUserInfoList);
        verify(model).addAttribute("accounts", combinedAccounts);
        verify(model).addAttribute("transferOtherAccounts", accountUserInfoList);
    }

    @Test
    void dashboard_WithClientError_ShouldAddErrorAttribute() {
        when(accountsClient.getUserInfoDto("testuser")).thenReturn(userInfo);
        when(accountsClient.getUserAccountsDto("testuser")).thenThrow(
            new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", "Error".getBytes(), null)
        );

        String viewName = mainController.dashboard(model);

        assertEquals("main", viewName);
        verify(model).addAttribute("ratesEndpoint", "/api/rates");
        verify(model).addAttribute("login", userInfo.login());
        verify(model).addAttribute("name", userInfo.name());
        verify(model).addAttribute("birthdate", userInfo.birthdate());
        verify(model).addAttribute("email", userInfo.email());
        verify(model).addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
    }

    @Test
    void getExchangeRates_ShouldReturnRates() {
        when(exchangeClient.getRates()).thenReturn(ratesDto);

        List<ExchangeRate> rates = mainController.getExchangeRates();

        assertEquals(ratesDto.rates(), rates);
    }
} 