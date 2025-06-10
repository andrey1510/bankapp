package com.accountservice.repositories;

import com.accountservice.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"accounts"})
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.accounts WHERE u.login <> :login")
    List<User> findAllExceptCurrentUserWithAccounts(@Param("login") String login);
}
