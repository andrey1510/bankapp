package com.transferservice.services;

import com.transferservice.clients.AccountClient;
import com.transferservice.clients.BlockerClient;
import com.transferservice.clients.ExchangeClient;
import com.transferservice.dto.BalanceUpdateRequestDto;
import com.transferservice.dto.ConversionRateDto;
import com.transferservice.dto.ConversionRateRequestDto;
import com.transferservice.dto.SuspicionOperationDto;
import com.transferservice.dto.TransferRequestDto;
import com.transferservice.exceptions.SameAccountTransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountClient accountClient;
    @Mock
    private BlockerClient blockerClient;
    @Mock
    private ExchangeClient exchangeClient;

    @InjectMocks
    private TransferServiceImpl transferService;

    private TransferRequestDto transferRequest;
    private BalanceUpdateRequestDto balanceUpdateRequest;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequestDto(
            "user@example.com",
            1L,
            "USD",
            new BigDecimal("100.00"),
            2L,
            "EUR",
            "login",
            "login"
        );

        balanceUpdateRequest = new BalanceUpdateRequestDto(
            1L,
            new BigDecimal("100.50"),
            2L,
            new BigDecimal("85.50"),
            "login",
            "login"
        );
    }

    @Test
    void processTransfer_ShouldThrowSameAccountException_WhenAccountsEqual() {
        TransferRequestDto invalidRequest = new TransferRequestDto(
            transferRequest.email(),
            1L,
            transferRequest.senderAccountCurrency(),
            transferRequest.amount(),
            1L,
            transferRequest.recipientAccountCurrency(),
            "login",
            "login"
        );

        assertThrows(SameAccountTransferException.class, () -> transferService.processTransfer(invalidRequest));
    }

    @Test
    void processTransfer_ShouldUseConversionRate_WhenCurrenciesDifferent() {
        when(exchangeClient.getConversionRate(any())).thenReturn(new ConversionRateDto(new BigDecimal("0.85")));
        when(blockerClient.checkTransferOperation(any())).thenReturn(new SuspicionOperationDto(false));

        transferService.processTransfer(transferRequest);

        verify(exchangeClient).getConversionRate(new ConversionRateRequestDto("USD", "EUR"));
        verify(accountClient).updateBalances(
            argThat(req -> req.recipientAccountBalanceChange()
                .compareTo(new BigDecimal("85.00")) == 0));
    }

    @Test
    void processTransfer_ShouldNotUseConversionRate_WhenCurrenciesSame() {
        TransferRequestDto sameCurrencyRequest = new TransferRequestDto(
            transferRequest.email(),
            transferRequest.senderAccountId(),
            "USD",
            transferRequest.amount(),
            transferRequest.recipientAccountId(),
            "USD",
            "login",
            "login"
        );

        when(blockerClient.checkTransferOperation(any())).thenReturn(new SuspicionOperationDto(false));

        transferService.processTransfer(sameCurrencyRequest);

        verifyNoInteractions(exchangeClient);
        verify(accountClient).updateBalances(argThat(req ->
            req.recipientAccountBalanceChange().equals(req.senderAccountBalanceChange())
        ));
    }

    @Test
    void processTransfer_ShouldFormatAmountCorrectly() {
        when(exchangeClient.getConversionRate(any()))
            .thenReturn(new ConversionRateDto(new BigDecimal("0.85")));
        when(blockerClient.checkTransferOperation(any()))
            .thenReturn(new SuspicionOperationDto(false));

        transferService.processTransfer(transferRequest);

        verify(accountClient).updateBalances(argThat(req ->
            req.recipientAccountBalanceChange().compareTo(new BigDecimal("85.00")) == 0));
    }

}
