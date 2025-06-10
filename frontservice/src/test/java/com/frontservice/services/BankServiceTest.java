package com.frontservice.services;

import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AccountUserInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.UserAccountsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    private BankService bankService;
    private UserAccountsDto userAccountsDto;
    private AccountInfoDto account1;
    private AccountInfoDto account2;
    private CurrenciesDto currenciesDto;

    @BeforeEach
    void setUp() {
        bankService = new BankService();

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

        userAccountsDto = new UserAccountsDto(
            "testuser",
            "Test User",
            "test@example.com",
            List.of(account1, account2)
        );

        currenciesDto = new CurrenciesDto(
            Map.of(
                "USD", "US Dollar",
                "EUR", "Euro",
                "RUR", "Ruble"
            )
        );
    }

    @Test
    void combineCurrencies_ShouldAddMissingCurrencies() {
        List<AccountInfoDto> result = bankService.combineCurrencies(userAccountsDto, currenciesDto);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(acc -> acc.currency().equals("USD")));
        assertTrue(result.stream().anyMatch(acc -> acc.currency().equals("EUR")));
        assertTrue(result.stream().anyMatch(acc -> acc.currency().equals("RUR")));
        assertTrue(result.stream().anyMatch(acc -> acc.currency().equals("RUR") && acc.amount()
            .equals(BigDecimal.ZERO)));
    }

    @Test
    void convertToAccountUserInfoList_ShouldConvertCorrectly() {
        List<AccountUserInfoDto> result = bankService.convertToAccountUserInfoList(userAccountsDto);

        assertEquals(2, result.size());
        assertEquals("Test User", result.getFirst().name());
        assertEquals("test@example.com", result.getFirst().email());
        assertEquals(1L, result.getFirst().accountId());
        assertEquals("Main Account", result.getFirst().title());
        assertEquals("USD", result.getFirst().currency());
        assertEquals(BigDecimal.valueOf(1000), result.getFirst().amount());
    }

    @Test
    void convertAllUsersToAccountInfo_WithValidData_ShouldConvertCorrectly() {
        AllUsersInfoExceptCurrentDto allUsersDto = new AllUsersInfoExceptCurrentDto(
            List.of(new UserAccountsDto(
                "otheruser",
                "Other User",
                "other@example.com",
                List.of(account2)
            ))
        );

        List<AccountUserInfoDto> result = bankService.convertAllUsersToAccountInfo(allUsersDto);

        assertEquals(1, result.size());
        assertEquals("Other User", result.getFirst().name());
        assertEquals("other@example.com", result.getFirst().email());
        assertEquals(2L, result.getFirst().accountId());
        assertEquals("Secondary Account", result.getFirst().title());
        assertEquals("EUR", result.getFirst().currency());
        assertEquals(BigDecimal.valueOf(500), result.getFirst().amount());
    }

    @Test
    void convertAllUsersToAccountInfo_WithNullData_ShouldReturnEmptyList() {
        List<AccountUserInfoDto> result = bankService.convertAllUsersToAccountInfo(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAccountById_WithExistingAccount_ShouldReturnAccount() {
        Optional<AccountInfoDto> result = bankService.findAccountById(userAccountsDto, 1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().accountId());
        assertEquals("Main Account", result.get().title());
        assertEquals("USD", result.get().currency());
    }

    @Test
    void findAccountById_WithNonExistingAccount_ShouldReturnEmpty() {
        Optional<AccountInfoDto> result = bankService.findAccountById(userAccountsDto, 999L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAccountById_WithMultipleUsers_ShouldFindAccount() {
        List<UserAccountsDto> users = List.of(
            userAccountsDto,
            new UserAccountsDto(
                "otheruser",
                "Other User",
                "other@example.com",
                List.of(account2)
            )
        );

        Optional<AccountInfoDto> result = bankService.findAccountById(users, 2L);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().accountId());
        assertEquals("Secondary Account", result.get().title());
        assertEquals("EUR", result.get().currency());
    }
} 