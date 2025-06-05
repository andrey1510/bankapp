package com.frontservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.CashRequestDto;
import com.frontservice.dto.CurrenciesDto;
import com.frontservice.dto.TransferRequestDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final RestTemplate restTemplate;

    @GetMapping("/main")
    public String dashboard(Model model) {
        //todo
        String login = "test";

        ResponseEntity<UserInfoDto> responseUser = restTemplate.getForEntity(
            "http://localhost:8881/api/users/user-info?login={login}",
            UserInfoDto.class,
            login
        );

        UserInfoDto userInfo = responseUser.getBody();
        model.addAttribute("login", userInfo.login());
        model.addAttribute("name", userInfo.name());
        model.addAttribute("birthdate", userInfo.birthdate());
        model.addAttribute("email", userInfo.email());

        try {
            ResponseEntity<UserAccountsDto> responseAccs = restTemplate.getForEntity(
                "http://localhost:8881/api/users/accounts-info?login={login}",
                UserAccountsDto.class,
                login
            );
            UserAccountsDto currentAccounts = responseAccs.getBody();

            model.addAttribute("transferAccounts", currentAccounts.accounts());

            ResponseEntity<CurrenciesDto> responseCurrencies = restTemplate.getForEntity(
                "http://localhost:8887/api/currencies",
                CurrenciesDto.class
            );
            CurrenciesDto currencies = responseCurrencies.getBody();

            List<AccountInfoDto> accounts = combineCurrencies(currentAccounts, currencies);

            model.addAttribute("accounts", accounts);


        } catch (HttpClientErrorException e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
        }

        return "main";
    }

    @PostMapping("/user/transfer-self")
    public String handleTransferSelfForm(
        @RequestParam String fromCurrency,
        @RequestParam String toCurrency,
        @RequestParam Double value,
        RedirectAttributes redirectAttributes) {

        //todo
        String login = "test";

        try {
            if (fromCurrency.equals(toCurrency)) {
                redirectAttributes.addFlashAttribute("transferErrors",
                    List.of("Нельзя переводить между счетами с одинаковой валютой"));
                return "redirect:/main";
            }

            if (value <= 0) {
                redirectAttributes.addFlashAttribute("transferErrors",
                    List.of("Сумма перевода должна быть положительной"));
                return "redirect:/main";
            }

            ResponseEntity<UserAccountsDto> accountsResponse = restTemplate.getForEntity(
                "http://localhost:8881/api/users/accounts-info?login={login}",
                UserAccountsDto.class,
                login
            );
            UserAccountsDto accountsDto = accountsResponse.getBody();


            AccountInfoDto senderAccount = accountsDto.accounts().stream()
                .filter(acc -> acc.currency().equals(fromCurrency) && acc.isExisting())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Счет отправителя не найден"));

            AccountInfoDto recipientAccount = accountsDto.accounts().stream()
                .filter(acc -> acc.currency().equals(toCurrency) && acc.isExisting())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Счет получателя не найден"));


            TransferRequestDto transferRequest = new TransferRequestDto(
                accountsDto.email(),
                senderAccount.accountId(),
                senderAccount.currency(),
                value,
                recipientAccount.accountId(),
                recipientAccount.currency()
            );

            ResponseEntity<Void> transferResponse = restTemplate.postForEntity(
                "http://localhost:8891/api/transfers/transfer",
                transferRequest,
                Void.class
            );

            if (!transferResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Ошибка при выполнении перевода");
            }

            redirectAttributes.addFlashAttribute("transferSuccess", true);
            return "redirect:/main";

        } catch (HttpClientErrorException e) {
            log.error("Transfer error", e);
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of("Ошибка при выполнении перевода: " + e.getStatusCode()));
            return "redirect:/main";
        } catch (Exception e) {
            log.error("Unexpected error", e);
            redirectAttributes.addFlashAttribute("transferErrors",
                List.of("Внутренняя ошибка сервера: " + e.getMessage()));
            return "redirect:/main";
        }
    }


    @PostMapping("/user/edit-user")
    public String handleEditUserForm(
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
        @RequestParam String email,
        RedirectAttributes redirectAttributes
    ) {
        //todo
        String login = "test";

        UserUpdateDto dto = new UserUpdateDto(login, name, birthdate, email);

        restTemplate.postForEntity(
            "http://localhost:8881/api/users/edit-user",
            dto,
            Void.class
        );
        redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены");

        return "redirect:/main";
    }

    @PostMapping("/user/edit-accounts")
    public String handleAccountsForm(
        @RequestParam(required = false) List<String> account,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        String login = "test";

        try {
            ResponseEntity<UserAccountsDto> response = restTemplate.getForEntity(
                "http://localhost:8881/api/users/accounts-info?login={login}",
                UserAccountsDto.class,
                login
            );
            UserAccountsDto currentAccounts = response.getBody();

            ResponseEntity<CurrenciesDto> responseCurrencies = restTemplate.getForEntity(
                "http://localhost:8887/api/currencies",
                CurrenciesDto.class
            );
            CurrenciesDto currencies = responseCurrencies.getBody();


            List<AccountInfoDto> allAccounts = combineCurrencies(currentAccounts, currencies);

            if (allAccounts == null || allAccounts.isEmpty()) {
                model.addAttribute("userAccountsErrors", List.of("Нет данных о счетах"));
                return "redirect:/main";
            }

            Set<String> updatedModel = account != null ? new HashSet<>(account) : Set.of();

            List<AccountInfoDto> updatedAccounts = allAccounts.stream()
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

            restTemplate.postForEntity(
                "http://localhost:8881/api/users/edit-accounts",
                new UserAccountsDto(login, null, updatedAccounts),
                Void.class
            );

            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены");
        } catch (HttpClientErrorException e) {
            handleErrorResponse(e, model);
            return "redirect:/main";
        } catch (Exception e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка сервера: " + e.getMessage()));
            return "redirect:/main";
        }

        return "redirect:/main";
    }

    @PostMapping("/user/cash")
    public String handleCashOperation(
        @RequestParam String currency,
        @RequestParam Double value,
        @RequestParam String action,
        Model model
    ) {

        //todo
        String login = "test";

        try {
            ResponseEntity<UserInfoDto> responseUser = restTemplate.getForEntity(
                "http://localhost:8881/api/users/user-info?login={login}",
                UserInfoDto.class,
                login
            );

            UserInfoDto userInfo = responseUser.getBody();
            model.addAttribute("login", userInfo.login());
            model.addAttribute("name", userInfo.name());
            model.addAttribute("birthdate", userInfo.birthdate());
            model.addAttribute("email", userInfo.email());

            ResponseEntity<UserAccountsDto> responseAccs = restTemplate.getForEntity(
                "http://localhost:8881/api/users/accounts-info?login={login}",
                UserAccountsDto.class,
                login
            );

            UserAccountsDto dto = responseAccs.getBody();
            model.addAttribute("accounts", dto.accounts());

        } catch (HttpClientErrorException e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
        }

        String email = (String) model.getAttribute("email");

        List<AccountInfoDto> accounts = (List<AccountInfoDto>) model.getAttribute("accounts");

        AccountInfoDto selectedAccount = accounts.stream()
            .filter(acc -> currency.equals(acc.currency()))
            .findFirst()
            .orElse(null);

        if (selectedAccount == null) {
            model.addAttribute("cashErrors", List.of("Выбранный счет не найден"));
            return "main";
        }

        boolean isDeposit = "PUT".equals(action);

        CashRequestDto request = new CashRequestDto(
            email,
            selectedAccount.accountId(),
            selectedAccount.currency(),
            value,
            isDeposit
        );

        log.info(String.valueOf(request.amount()));

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:8883/api/cash/operation",
                request,
                Void.class
            );

            return "redirect:/main";

        } catch (HttpClientErrorException e) {

            log.warn("HttpClientErrorException e");

            handleCashServiceError(e, model);
            return "redirect:/main";
        }
    }


    private void handleErrorResponse(HttpClientErrorException e, Model model) {
        try {
            List<String> errors = new ObjectMapper().readValue(
                e.getResponseBodyAsString(),
                new TypeReference<List<String>>() {}
            );
            model.addAttribute("userAccountsErrors", errors);
        } catch (JsonProcessingException ex) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка обработки данных"));
        }
    }

    private void handleCashServiceError(HttpClientErrorException e, Model model) {
        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
            String errorMessage = e.getResponseBodyAsString();

            log.warn(errorMessage);

            model.addAttribute("cashErrors", List.of(errorMessage));
        } else {
            model.addAttribute("cashErrors",
                List.of("Ошибка обработки операции: " + e.getStatusCode()));
        }
    }


    public List<AccountInfoDto> combineCurrencies(
        UserAccountsDto userAccountsDto,
        CurrenciesDto currenciesDto) {

        List<AccountInfoDto> allAccounts = new ArrayList<>(userAccountsDto.accounts());

        Set<String> existingCurrencies = userAccountsDto.accounts().stream()
            .map(AccountInfoDto::currency)
            .collect(Collectors.toSet());

        currenciesDto.currencies().forEach((currencyName, currencyTitle) -> {
            if (!existingCurrencies.contains(currencyName)) {
                allAccounts.add(new AccountInfoDto(
                    null,
                    currencyTitle,
                    currencyName,
                    0.0,
                    false
                ));
            }
        });

        return allAccounts;
    }

}


