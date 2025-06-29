package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.clients.CashClient;
import com.frontservice.clients.ExchangeClient;
import com.frontservice.clients.TransferClient;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.CashRequestDto;
import com.frontservice.dto.TransferRequestDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserUpdateDto;
import com.frontservice.exceptions.AccountNotFoundException;
import com.frontservice.services.AuthService;
import com.frontservice.services.BankService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BankController {

    private final AccountsClient accountsClient;
    private final TransferClient transferClient;
    private final ExchangeClient exchangeClient;
    private final CashClient cashClient;

    private final BankService bankService;
    private final AuthService authService;

    private final MeterRegistry meterRegistry;

    @PostMapping("/user/transfer-self")
    @PreAuthorize("hasRole('USER')")
    public String transferToSelf(
        @RequestParam Long fromAccount,
        @RequestParam Long toAccount,
        @RequestParam BigDecimal value,
        RedirectAttributes redirectAttributes) {

        UserAccountsDto currentAccounts = accountsClient.getUserAccountsDto(authService.getLoginFromSecurityContext());

        AccountInfoDto senderAccount = bankService.findAccountById(currentAccounts, fromAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));

        AccountInfoDto recipientAccount = bankService.findAccountById(currentAccounts, toAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        try {
            transferClient.sendTransferRequest(new TransferRequestDto(
                currentAccounts.email(),
                senderAccount.accountId(),
                senderAccount.currency(),
                value,
                recipientAccount.accountId(),
                recipientAccount.currency(),
                currentAccounts.login(),
                currentAccounts.login()
            ));

            redirectAttributes.addFlashAttribute("transferSuccess", "Успешный перевод");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            meterRegistry.counter("transfer_failed",
                "sender_login", currentAccounts.login(),
                "recipient_login", currentAccounts.login(),
                "sender_account", senderAccount.accountId().toString(),
                "recipient_account", senderAccount.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error(e.getMessage());
            meterRegistry.counter("transfer_failed",
                "sender_login", currentAccounts.login(),
                "recipient_login", currentAccounts.login(),
                "sender_account", senderAccount.accountId().toString(),
                "recipient_account", senderAccount.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

    @PostMapping("/user/transfer-other")
    @PreAuthorize("hasRole('USER')")
    public String transferToOther(
        @RequestParam Long fromAccount,
        @RequestParam Long toAccount,
        @RequestParam BigDecimal value,
        RedirectAttributes redirectAttributes){

        UserAccountsDto currentAccounts = accountsClient.getUserAccountsDto(authService.getLoginFromSecurityContext());

        AccountInfoDto senderAccount = bankService.findAccountById(currentAccounts, fromAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));

        List<UserAccountsDto> users = accountsClient
            .getAllUsersInfoExceptCurrentDto(authService.getLoginFromSecurityContext()).users();

        AccountInfoDto recipientAccount = bankService.findAccountById(users, toAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        String recipientLogin = bankService.findLoginByAccountId(users, fromAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        try {
            transferClient.sendTransferRequest(new TransferRequestDto(
                currentAccounts.email(),
                senderAccount.accountId(),
                senderAccount.currency(),
                value,
                recipientAccount.accountId(),
                recipientAccount.currency(),
                currentAccounts.login(),
                recipientLogin
            ));

            redirectAttributes.addFlashAttribute("transferOtherSuccess", "Успешный перевод");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            meterRegistry.counter("transfer_failed",
                "sender_login", currentAccounts.login(),
                "recipient_login", recipientLogin,
                "sender_account", senderAccount.accountId().toString(),
                "recipient_account", recipientAccount.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("transferOtherErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error(e.getMessage());
            meterRegistry.counter("transfer_failed",
                "sender_login", currentAccounts.login(),
                "recipient_login", currentAccounts.login(),
                "sender_account", senderAccount.accountId().toString(),
                "recipient_account", senderAccount.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("transferOtherErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

    @PostMapping("/user/edit-user")
    @PreAuthorize("hasRole('USER')")
    public String editUser(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
        @RequestParam String email,
        RedirectAttributes redirectAttributes
    ) {

        UserUpdateDto dto = new UserUpdateDto(authService.getLoginFromSecurityContext(), name, birthdate, email);

        try {
            accountsClient.sendUserUpdateRequest(dto);

            redirectAttributes.addFlashAttribute("successUpdatedUser", "Данные успешно обновлены");
        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            redirectAttributes.addFlashAttribute("errorUsers",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("errorUsers",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }

        return "redirect:/main";
    }

    @PostMapping("/user/edit-accounts")
    @PreAuthorize("hasRole('USER')")
    public String editAccounts(
        @RequestParam(required = false) List<String> account,
        RedirectAttributes redirectAttributes
    ) {

        Set<String> updatedModel = account != null ? new HashSet<>(account) : Set.of();
        List<AccountInfoDto> updatedAccounts = bankService.combineCurrencies(
                accountsClient.getUserAccountsDto(authService.getLoginFromSecurityContext()), exchangeClient.getCurrenciesDto()).stream()
            .map(acc -> {
                boolean enabled = updatedModel.contains(acc.currency());
                return new AccountInfoDto(
                    acc.accountId(),
                    acc.title(),
                    acc.currency(),
                    acc.amount(),
                    enabled
                );
            })
            .collect(Collectors.toList());

        try {
            accountsClient.sendAccountsUpdateRequest(authService.getLoginFromSecurityContext(), updatedAccounts);

            redirectAttributes.addFlashAttribute("successUpdatedAcc", "Данные обновлены");

        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            redirectAttributes.addFlashAttribute("userAccountsErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("userAccountsErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }

        return "redirect:/main";
    }

    @PostMapping("/user/cash")
    @PreAuthorize("hasRole('USER')")
    public String processCash(
        @RequestParam Long accountId,
        @RequestParam BigDecimal value,
        @RequestParam String action,
        RedirectAttributes redirectAttributes
    ) {

        UserAccountsDto currentAccounts = accountsClient.getUserAccountsDto(authService.getLoginFromSecurityContext());

        AccountInfoDto account = bankService.findAccountById(currentAccounts, accountId)
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден"));

        try {
            cashClient.sendCashRequest(new CashRequestDto(
                currentAccounts.email(),
                account.accountId(),
                account.currency(),
                value,
                "PUT".equals(action),
                currentAccounts.login()
            ));

            redirectAttributes.addFlashAttribute("cashSuccess", "Операция успешна");

            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            meterRegistry.counter("cash_failed",
                "login", currentAccounts.login(),
                "account", account.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("cashErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error("Unexpected error", e);
            meterRegistry.counter("cash_failed",
                "login", currentAccounts.login(),
                "account", account.accountId().toString()
            ).increment();
            redirectAttributes.addFlashAttribute("cashErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

}
