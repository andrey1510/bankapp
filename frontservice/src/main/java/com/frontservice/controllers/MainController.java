package com.frontservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontservice.dto.AccountInfoDto;
import com.frontservice.dto.UserAccountsDto;
import com.frontservice.dto.UserInfoDto;
import com.frontservice.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

            UserAccountsDto dto = responseAccs.getBody();
            List<AccountInfoDto> accounts = dto.accounts();


            model.addAttribute("accounts", accounts);

        } catch (HttpClientErrorException e) {
            model.addAttribute("userAccountsErrors", List.of("Ошибка загрузки данных"));
        }

        return "main";
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
            if (currentAccounts == null || currentAccounts.accounts() == null) {
                model.addAttribute("userAccountsErrors", List.of("Нет данных о счетах"));
                return "redirect:/main";
            }

            Set<String> enabledCurrencies = account != null ? new HashSet<>(account) : Set.of();

            List<AccountInfoDto> updatedAccounts = currentAccounts.accounts().stream()
                .map(acc -> {
                    boolean enabled = enabledCurrencies.contains(acc.currency());
                    return new AccountInfoDto(
                        acc.accountId(),
                        acc.title(),
                        acc.currency(),
                        enabled ? acc.amount() : 0.0,
                        enabled
                    );
                })
                .collect(Collectors.toList());

            restTemplate.postForEntity(
                "http://localhost:8881/api/users/edit-accounts",
                new UserAccountsDto(login, updatedAccounts),
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

//
//    @PostMapping("/user/edit-accounts")
//    public String handleCashForm(
//        @RequestParam(required = false) List<String> account,
//        Model model,
//        RedirectAttributes redirectAttributes
//    ) {
//        String login = "test";
//
//        try {
//            ResponseEntity<UserAccountsDto> response = restTemplate.getForEntity(
//                "http://localhost:8881/api/users/accounts-info?login={login}",
//                UserAccountsDto.class,
//                login
//            );
//
//            UserAccountsDto currentAccounts = response.getBody();
//            if (currentAccounts == null || currentAccounts.accounts() == null) {
//                model.addAttribute("userAccountsErrors", List.of("Нет данных о счетах"));
//                return "redirect:/main";
//            }
//
//            Set<String> enabledCurrencies = account != null ? new HashSet<>(account) : Set.of();
//
//            List<AccountInfoDto> updatedAccounts = currentAccounts.accounts().stream()
//                .map(acc -> {
//                    boolean enabled = enabledCurrencies.contains(acc.currency());
//                    return new AccountInfoDto(
//                        acc.accountId(),
//                        acc.title(),
//                        acc.currency(),
//                        enabled ? acc.amount() : 0.0,
//                        enabled
//                    );
//                })
//                .collect(Collectors.toList());
//
//            restTemplate.postForEntity(
//                "http://localhost:8881/api/users/edit-accounts",
//                new UserAccountsDto(login, updatedAccounts),
//                Void.class
//            );
//
//            redirectAttributes.addFlashAttribute("success", "Данные успешно обновлены");
//        } catch (HttpClientErrorException e) {
//            handleErrorResponse(e, model);
//            return "redirect:/main";
//        } catch (Exception e) {
//            model.addAttribute("userAccountsErrors", List.of("Ошибка сервера: " + e.getMessage()));
//            return "redirect:/main";
//        }
//
//        return "redirect:/main";
//    }
//
//
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



}


