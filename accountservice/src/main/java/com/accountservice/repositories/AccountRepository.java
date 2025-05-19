package com.accountservice.repositories;

import com.accountservice.entities.Account;
import com.accountservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUserId(Long accountId);

    boolean existsByUserAndCurrency(User user, String currency);

    Optional<Account> findByIdAndUser(UUID id, User user);
}
