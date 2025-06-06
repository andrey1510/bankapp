package com.frontservice.controllers;

import com.frontservice.clients.MainClient;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.CashRequestDto;
import com.frontservice.dto.PasswordChangeDto;
import com.frontservice.dto.TransferRequestDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import com.frontservice.exceptions.AccountNotFoundException;
import com.frontservice.exceptions.PasswordsAreNotEqualException;
import com.frontservice.services.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    String logintest = "test";

    private final MainClient mainClient;
    private final RestTemplate restTemplate;
    private final MainService mainService;

    @GetMapping("/main")
    public String dashboard(Model model) {
        //todo
        String login = logintest;

        UserInfoDto userInfo = mainClient.getUserInfoDto(login);

        model.addAttribute("login", userInfo.login());
        model.addAttribute("name", userInfo.name());
        model.addAttribute("birthdate", userInfo.birthdate());
        model.addAttribute("email", userInfo.email());

        try {
            UserAccountsDto currentAccounts = mainClient.getUserAccountsDto(login);

            model.addAttribute("transferAccounts",
                mainService.convertToAccountUserInfoList(currentAccounts));
            model.addAttribute("accounts",
                mainService.combineCurrencies(currentAccounts, mainClient.getCurrenciesDto()));
            model.addAttribute("transferOtherAccounts",
                mainService.convertAllUsersToAccountInfo(mainClient.getAllUsersInfoExceptCurrentDto(login)));

        } catch (HttpClientErrorException e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
        }

        return "main";
    }


    @PostMapping("/user/transfer-self")
    public String transferToSelf(
        @RequestParam Long fromAccount,
        @RequestParam Long toAccount,
        @RequestParam Double value,
        RedirectAttributes redirectAttributes) {

        //todo
        String login  = logintest;

        UserAccountsDto currentAccounts = mainClient.getUserAccountsDto(login);

        AccountInfoDto senderAccount = mainService.findAccountById(currentAccounts, fromAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));

        AccountInfoDto recipientAccount = mainService.findAccountById(currentAccounts, toAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        TransferRequestDto transferRequest = new TransferRequestDto(
            currentAccounts.email(),
            senderAccount.accountId(),
            senderAccount.currency(),
            value,
            recipientAccount.accountId(),
            recipientAccount.currency()
        );

        try {
            restTemplate.postForEntity(
                "http://localhost:8891/api/transfers/transfer",
                transferRequest,
                Void.class
            );

            redirectAttributes.addFlashAttribute("transferSuccess", "Успешный перевод");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

    @PostMapping("/user/transfer-other")
    public String transferToOther(
        @RequestParam Long fromAccount,
        @RequestParam Long toAccount,
        @RequestParam Double value,
        RedirectAttributes redirectAttributes){

        //todo
        String login  = logintest;

        UserAccountsDto currentAccounts = mainClient.getUserAccountsDto(login);

        AccountInfoDto senderAccount = mainService.findAccountById(currentAccounts, fromAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));

        AccountInfoDto recipientAccount = mainService.findAccountById(
            mainClient.getAllUsersInfoExceptCurrentDto(login).users(), toAccount)
            .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        TransferRequestDto transferRequest = new TransferRequestDto(
            currentAccounts.email(),
            senderAccount.accountId(),
            senderAccount.currency(),
            value,
            recipientAccount.accountId(),
            recipientAccount.currency()
        );

        try {
            restTemplate.postForEntity(
                "http://localhost:8891/api/transfers/transfer",
                transferRequest,
                Void.class
            );

            redirectAttributes.addFlashAttribute("transferOtherSuccess", "Успешный перевод");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("transferOtherErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("transferOtherErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

    @PostMapping("/user/edit-user")
    public String editUser(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
        @RequestParam String email,
        RedirectAttributes redirectAttributes
    ) {
        //todo
        String login  = logintest;

        UserUpdateDto dto = new UserUpdateDto(login, name, birthdate, email);

        try {
            restTemplate.postForEntity(
                "http://localhost:8881/api/users/edit-user",
                dto,
                Void.class
            );
            redirectAttributes.addFlashAttribute("successUpdatedUser", "Данные успешно обновлены");
        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("errorUsers",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorUsers",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }

        return "redirect:/main";
    }

    @PostMapping("/user/edit-accounts")
    public String editAccounts(
        @RequestParam(required = false) List<String> account,
        RedirectAttributes redirectAttributes
    ) {
        String login = "test";

            Set<String> updatedModel = account != null ? new HashSet<>(account) : Set.of();
            List<AccountInfoDto> updatedAccounts = mainService.combineCurrencies(mainClient.getUserAccountsDto(login), mainClient.getCurrenciesDto()).stream()
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

            restTemplate.postForEntity(
                "http://localhost:8881/api/users/edit-accounts",
                new UserAccountsDto(login, null, null, updatedAccounts),
                Void.class
            );

            redirectAttributes.addFlashAttribute("successUpdatedAcc", "Данные успешно обновлены");

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("userAccountsErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("userAccountsErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }

        return "redirect:/main";
    }

    @PostMapping("/user/cash")
    public String processCash(
        @RequestParam Long accountId,
        @RequestParam Double value,
        @RequestParam String action,
        RedirectAttributes redirectAttributes
    ) {

        //todo
        String login  = logintest;

        UserAccountsDto currentAccounts = mainClient.getUserAccountsDto(login);

        AccountInfoDto account = mainService.findAccountById(currentAccounts, accountId)
            .orElseThrow(() -> new AccountNotFoundException("Счет не найден"));

        CashRequestDto request = new CashRequestDto(
            currentAccounts.email(),
            account.accountId(),
            account.currency(),
            value,
            "PUT".equals(action)
        );

        try {
            restTemplate.postForEntity(
                "http://localhost:8883/api/cash/operation",
                request,
                Void.class
            );

            redirectAttributes.addFlashAttribute("cashSuccess", "Операция успешна");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("cashErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error("Unexpected error", e);
            redirectAttributes.addFlashAttribute("cashErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }
    }

    @PostMapping("/user/editPassword")
    public String editPassword(
        @RequestParam String password,
        @RequestParam String repeat,
        RedirectAttributes redirectAttributes
    ) {

        //todo
        String login  = logintest;

        try {

            if (!Objects.equals(password, repeat))
                throw new PasswordsAreNotEqualException("Пароли должны совпадать");

            restTemplate.postForEntity(
                "http://localhost:8881/api/users/change-password",
                new PasswordChangeDto(login, password),
                Void.class
            );

            redirectAttributes.addFlashAttribute("passwordChangeSuccess", "Пароль изменен");
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            redirectAttributes.addFlashAttribute("passwordErrors",
                List.of(e.getResponseBodyAsString()));
            return "redirect:/main";
        } catch (PasswordsAreNotEqualException e) {
            redirectAttributes.addFlashAttribute("passwordErrors",
                List.of(e.getMessage()));
            return "redirect:/main";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordErrors",
                List.of("Внутренняя ошибка сервера"));
            return "redirect:/main";
        }

    }


}


