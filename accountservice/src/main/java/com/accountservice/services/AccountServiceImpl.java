package com.accountservice.services;

import com.accountservice.dto.AccountDTO;
import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import com.accountservice.repositories.AccountRepository;
import com.accountservice.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(Long userId, AccountDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException());

       Account account = new Account();
        account.setAmount(dto.amount());
        account.setCurrency(dto.currency());
        account.setUser(user);

        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(UUID id, Long userId, AccountDTO dto) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException());

        account.setAmount(dto.amount());
        account.setCurrency(dto.currency());
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(UUID id, Long userId) {
        Account account = accountRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new EntityNotFoundException());
        accountRepository.delete(account);
    }
}
