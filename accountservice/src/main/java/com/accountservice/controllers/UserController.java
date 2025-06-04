package com.accountservice.controllers;

import com.accountservice.dto.AccountInfoDto;
import com.accountservice.dto.UserAccountsDto;
import com.accountservice.dto.UserDto;
import com.accountservice.dto.UserInfoDto;
import com.accountservice.dto.UserUpdateDto;
import com.accountservice.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        } catch (IllegalArgumentException e) {
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
        return ResponseEntity.ok(userService.updateUser(dto.login(), dto));
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
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Ошибка сервера");
        }
    }
}


