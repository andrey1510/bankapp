package com.frontservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontservice.dto.UserDto;
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
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignupController {

    private final RestTemplate restTemplate;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        if (!model.containsAttribute("login")) model.addAttribute("login", "");
        if (!model.containsAttribute("name")) model.addAttribute("name", "");
        if (!model.containsAttribute("birthdate")) model.addAttribute("birthdate", null);
        if (!model.containsAttribute("email")) model.addAttribute("email", "");
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(
        @RequestParam String login,
        @RequestParam String password,
        @RequestParam String confirm_password,
        @RequestParam String name,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
        @RequestParam String email,
        Model model,
        RedirectAttributes redirectAttributes
    ) {

        if (!password.equals(confirm_password)) {
            model.addAttribute("errors", List.of("Пароли не совпадают"));
            return populateModel(model, login, name, birthdate, email);
        }

        UserDto dto = new UserDto(login, password, name, birthdate, email);

        try {

            restTemplate.postForEntity(
                "http://localhost:8881/api/users/signup",
                dto,
                Void.class
            );
            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно!");
            return "redirect:/login";
        } catch (HttpClientErrorException e) {

            try {
                List<String> errors = new ObjectMapper().readValue(
                    e.getResponseBodyAsString(),
                    new TypeReference<List<String>>() {}
                );
                model.addAttribute("errors", errors);
            } catch (JsonProcessingException ex) {
                model.addAttribute("errors", List.of("Ошибка сервера"));
            }
            return populateModel(model, login, name, birthdate, email);
        }
    }

    private String populateModel(Model model, String login, String name,
                                 LocalDate birthdate, String email) {
        model.addAttribute("login", login);
        model.addAttribute("name", name);
        model.addAttribute("birthdate", birthdate);
        model.addAttribute("email", email);
        return "signup";
    }



}
