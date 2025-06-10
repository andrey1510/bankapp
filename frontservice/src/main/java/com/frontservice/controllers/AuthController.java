package com.frontservice.controllers;

import com.frontservice.clients.AccountsClient;
import com.frontservice.dto.UserDto;
import com.frontservice.exceptions.PasswordsAreNotEqualException;
import com.frontservice.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AccountsClient accountsClient;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {

        if (error != null)
            model.addAttribute("error", "Неверный логин или пароль");

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null)
            new SecurityContextLogoutHandler().logout(request, response, auth);

        return "redirect:/login";
    }

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
            accountsClient.sendSignupRequest(dto);

            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно!");
            return "redirect:/login";

        } catch (HttpClientErrorException e) {
            model.addAttribute("errors",
                List.of(e.getResponseBodyAsString() != null ? e.getResponseBodyAsString() : "Ошибка сервера"));
            return populateModel(model, login, name, birthdate, email);
        }
    }

    @PostMapping("/user/change-password")
    @PreAuthorize("hasRole('USER')")
    public String changePassword(
        @RequestParam String password,
        @RequestParam String repeat,
        RedirectAttributes redirectAttributes
    ) {

        try {

            if (!Objects.equals(password, repeat))
                throw new PasswordsAreNotEqualException("Пароли должны совпадать");

            accountsClient.sendPasswordChangeRequest(
                password, authService.getLoginFromSecurityContext());

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

    private String populateModel(Model model, String login, String name,
                                 LocalDate birthdate, String email) {
        model.addAttribute("login", login);
        model.addAttribute("name", name);
        model.addAttribute("birthdate", birthdate);
        model.addAttribute("email", email);
        return "signup";
    }

}
