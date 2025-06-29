package com.frontservice.services;

import com.frontservice.clients.AccountsClient;
import com.frontservice.dto.LoginPasswordDto;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteAuthenticationProvider implements AuthenticationProvider {

    private final AccountsClient accountsClient;
    private final MeterRegistry meterRegistry;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            ResponseEntity<Void> response = accountsClient.sendAuthRequest(
                new LoginPasswordDto(login, password)
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                UsernamePasswordAuthenticationToken roleUser = new UsernamePasswordAuthenticationToken(
                    login,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                log.info("Authenticated user: " + login);
                meterRegistry.counter("user_login",
                    "login", login,
                    "status", "success"
                ).increment();
                return roleUser;
            } else {
                log.warn("Authentication failed for {}: HTTP {}", login, response.getStatusCodeValue());


            }
        } catch (HttpClientErrorException e) {
            log.error("Authentication error for {}: {}", login, e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Authentication error for {}", login, e);
        }

        meterRegistry.counter("user_login",
            "login", login,
            "status", "failed").increment();
        throw new BadCredentialsException("Неверный логин или пароль");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}