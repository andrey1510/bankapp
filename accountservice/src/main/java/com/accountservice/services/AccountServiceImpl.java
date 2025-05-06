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

    public Account createBankAccount(Long accountId, AccountDTO dto) {
        User user = userRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException());

       Account account = new Account();
        account.setAmount(dto.amount());
        account.setCurrency(dto.currency());
        account.setUser(user);

        return accountRepository.save(account);
    }

    public Account updateBankAccount(UUID id, Long accountId, AccountDTO dto) {
        Account bankAccount = accountRepository.findByIdAndAccountId(id, accountId)
            .orElseThrow(() -> new EntityNotFoundException());

        bankAccount.setAmount(dto.amount());
        bankAccount.setCurrency(dto.currency());
        return accountRepository.save(bankAccount);
    }

    public void deleteBankAccount(UUID id, Long accountId) {
        Account account = accountRepository.findByIdAndAccountId(id, accountId)
            .orElseThrow(() -> new EntityNotFoundException());
        accountRepository.delete(account);
    }
}
