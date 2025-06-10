package com.frontservice.services;

import com.frontservice.clients.AccountsClient;
import com.frontservice.dto.LoginPasswordDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoteAuthenticationProviderTest {

    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private RemoteAuthenticationProvider authenticationProvider;

    private UsernamePasswordAuthenticationToken authentication;

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken("testuser", "password");
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnAuthenticatedToken() {
        when(accountsClient.sendAuthRequest(any(LoginPasswordDto.class)))
            .thenReturn(ResponseEntity.ok().build());

        var result = authenticationProvider.authenticate(authentication);

        assertTrue(result.isAuthenticated());
        assertEquals("testuser", result.getName());
        assertEquals(1, result.getAuthorities().size());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowBadCredentialsException() {
        when(accountsClient.sendAuthRequest(any(LoginPasswordDto.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        assertThrows(BadCredentialsException.class, () -> 
            authenticationProvider.authenticate(authentication)
        );
    }

    @Test
    void authenticate_WithHttpClientError_ShouldThrowBadCredentialsException() {
        when(accountsClient.sendAuthRequest(any(LoginPasswordDto.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        assertThrows(BadCredentialsException.class, () -> 
            authenticationProvider.authenticate(authentication)
        );
    }

    @Test
    void authenticate_WithGeneralError_ShouldThrowBadCredentialsException() {
        when(accountsClient.sendAuthRequest(any(LoginPasswordDto.class)))
            .thenThrow(new RuntimeException("Connection error"));

        assertThrows(BadCredentialsException.class, () -> 
            authenticationProvider.authenticate(authentication)
        );
    }

    @Test
    void supports_WithUsernamePasswordToken_ShouldReturnTrue() {
        assertTrue(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void supports_WithOtherToken_ShouldReturnFalse() {
        assertFalse(authenticationProvider.supports(Object.class));
    }
} 