package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.CashClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.clients.TransferClient;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.AllUsersInfoExceptCurrentDto;
import com.frontservice.dto.CashRequestDto;
import com.frontservice.dto.TransferRequestDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserUpdateDto;
import com.frontservice.exceptions.AccountNotFoundException;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private TransferClient transferClient;

    @Mock
    private CashClient cashClient;

    @Mock
    private ExchangeClient exchangeClient;

    @Mock
    private BankService bankService;

    @Mock
    private AuthService authService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private BankController bankController;

    private UserAccountsDto userAccountsDto;
    private AccountInfoDto account1;
    private AccountInfoDto account2;
    private AllUsersInfoExceptCurrentDto allUsersInfo;

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

        when(authService.getLoginFromSecurityContext()).thenReturn("testuser");
    }

    @Test
    void transferToSelf_WithValidData_ShouldTransfer() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        when(bankService.findAccountById((UserAccountsDto) any(), any())).thenReturn(Optional.of(account1));
        doNothing().when(transferClient).sendTransferRequest(any());

        assertEquals("redirect:/main", bankController.transferToSelf(
            1L,
            2L,
            BigDecimal.valueOf(100),
            redirectAttributes
        ));

        verify(transferClient).sendTransferRequest(any(TransferRequestDto.class));
        verify(redirectAttributes).addFlashAttribute("transferSuccess", "Успешный перевод");
    }

    @Test
    void transferToSelf_WithAccountNotFound_ShouldThrowException() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        when(bankService.findAccountById((UserAccountsDto) any(), any())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
            bankController.transferToSelf(
                1L,
                2L,
                BigDecimal.valueOf(100),
                redirectAttributes
            )
        );
    }

    @Test
    void transferToSelf_WithClientError_ShouldReturnError() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        when(bankService.findAccountById((UserAccountsDto) any(), any())).thenReturn(Optional.of(account1));
        doThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, "Insufficient funds".getBytes(), null))
            .when(transferClient).sendTransferRequest(any());

        assertEquals("redirect:/main", bankController.transferToSelf(
            1L,
            2L,
            BigDecimal.valueOf(100),
            redirectAttributes
        ));

        verify(redirectAttributes).addFlashAttribute("transferErrors", List.of("Insufficient funds"));
    }

    @Test
    void transferToOther_WithValidData_ShouldTransfer() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        when(accountsClient.getAllUsersInfoExceptCurrentDto(anyString())).thenReturn(allUsersInfo);
        when(bankService.findAccountById(any(UserAccountsDto.class), any())).thenReturn(Optional.of(account1));
        when(bankService.findAccountById(any(List.class), any())).thenReturn(Optional.of(account2));
        doNothing().when(transferClient).sendTransferRequest(any());

        assertEquals("redirect:/main", bankController.transferToOther(
            1L,
            2L,
            BigDecimal.valueOf(100),
            redirectAttributes
        ));

        verify(transferClient).sendTransferRequest(any(TransferRequestDto.class));
        verify(redirectAttributes).addFlashAttribute("transferOtherSuccess", "Успешный перевод");
    }

    @Test
    void editUser_WithValidData_ShouldUpdateUser() {
        doNothing().when(accountsClient).sendUserUpdateRequest(any());

        assertEquals("redirect:/main", bankController.editUser(
            "Updated Name",
            LocalDate.now().minusYears(20),
            "updated@example.com",
            redirectAttributes
        ));

        verify(accountsClient).sendUserUpdateRequest(any(UserUpdateDto.class));
        verify(redirectAttributes).addFlashAttribute("successUpdatedUser", "Данные успешно обновлены");
    }

    @Test
    void editUser_WithClientError_ShouldReturnError() {
        doThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, "Invalid email".getBytes(), null))
            .when(accountsClient).sendUserUpdateRequest(any());

        assertEquals("redirect:/main", bankController.editUser(
            "Updated Name",
            LocalDate.now().minusYears(20),
            "updated@example.com",
            redirectAttributes
        ));

        verify(redirectAttributes).addFlashAttribute("errorUsers", List.of("Invalid email"));
    }

    @Test
    void editAccounts_WithValidData_ShouldUpdateAccounts() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        doNothing().when(accountsClient).sendAccountsUpdateRequest(anyString(), any());

        assertEquals("redirect:/main", bankController.editAccounts(
            List.of("USD", "EUR"),
            redirectAttributes
        ));

        verify(accountsClient).sendAccountsUpdateRequest(anyString(), any());
        verify(redirectAttributes).addFlashAttribute("successUpdatedAcc", "Данные обновлены");
    }

    @Test
    void editAccounts_WithClientError_ShouldReturnError() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        doThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, "Invalid accounts".getBytes(), null))
            .when(accountsClient).sendAccountsUpdateRequest(anyString(), any());

        assertEquals("redirect:/main", bankController.editAccounts(
            List.of("USD", "EUR"),
            redirectAttributes
        ));

        verify(redirectAttributes).addFlashAttribute("userAccountsErrors", List.of("Invalid accounts"));
    }

    @Test
    void processCash_WithValidData_ShouldProcessCash() {
        when(accountsClient.getUserAccountsDto(anyString())).thenReturn(userAccountsDto);
        when(bankService.findAccountById(any(UserAccountsDto.class), any())).thenReturn(Optional.of(account1));
        doNothing().when(cashClient).sendCashRequest(any());

        assertEquals("redirect:/main", bankController.processCash(
            1L,
            BigDecimal.valueOf(100),
            "PUT",
            redirectAttributes
        ));

        verify(cashClient).sendCashRequest(any(CashRequestDto.class));
        verify(redirectAttributes).addFlashAttribute("cashSuccess", "Операция успешна");
    }

} 