package com.accountservice.controllers;

import com.accountservice.dto.AllUsersInfoExceptCurrentDto;
import com.accountservice.dto.PasswordChangeDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.exceptions.AccountWithSuchCurrencyAlreadyExists;
import com.accountservice.exceptions.EmailAlreadyExistsException;
import com.accountservice.exceptions.InsufficientFundsException;
import com.accountservice.exceptions.LoginAlreadyExistsException;
import com.accountservice.exceptions.NotNullBalanceException;
import com.accountservice.exceptions.WrongAgeException;
import com.accountservice.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList())
            );
        }

        try {
            userService.createUser(userDto);
            return ResponseEntity.ok().build();
        } catch (EmailAlreadyExistsException | LoginAlreadyExistsException | InsufficientFundsException |
                 WrongAgeException e  ) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList())
            );
        }

        try {
            userService.changePassword(passwordChangeDto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e  ) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserInfoDto> getUserInfo(@RequestParam String login) {
        return ResponseEntity.ok(userService.getUserInfo(login));
    }

    @PostMapping("/edit-user")
    public ResponseEntity<?> updateUser(
        @Valid @RequestBody UserUpdateDto dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList()
            );
        }

        try {
            return ResponseEntity.ok(userService.updateUser(dto.login(), dto));
        } catch (EmailAlreadyExistsException | WrongAgeException e  ) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        }

    }

    @GetMapping("/accounts-info")
    public ResponseEntity<UserAccountsDto> getAccountsInfo(@RequestParam String login) {
        return ResponseEntity.ok(userService.getAccountsInfo(login));
    }

    @PostMapping("/edit-accounts")
    public ResponseEntity<?> updateAccounts(
        @Valid @RequestBody UserAccountsDto dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(
                bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList())
            );
        }

        try {
            userService.updateAccounts(dto);
            return ResponseEntity.ok().build();

        } catch (NotNullBalanceException | AccountWithSuchCurrencyAlreadyExists e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка сервера");
        }
    }

    @GetMapping("/users-except-current")
    public ResponseEntity<AllUsersInfoExceptCurrentDto> getAllUsersExceptCurrent(@RequestParam String login) {
        return ResponseEntity.ok(userService.getAllUsersInfoExceptCurrent(login));
    }

}


