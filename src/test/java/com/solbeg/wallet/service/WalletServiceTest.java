package com.solbeg.wallet.service;

import com.solbeg.wallet.dto.WalletAddAmountRequest;
import com.solbeg.wallet.dto.WalletRemoveAmountRequest;
import com.solbeg.wallet.dto.WalletTransferAmountRequest;
import com.solbeg.wallet.exceptions.RestException;
import com.solbeg.wallet.mapper.WalletMapper;
import com.solbeg.wallet.model.Wallet;
import com.solbeg.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletMapper walletMapper;

    @InjectMocks
    WalletService walletService;

    @Test
    void testAddAmountToWallet() {
        //given
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("100.01"));

        WalletAddAmountRequest request = new WalletAddAmountRequest();
        request.setWalletId(1L);
        request.setAmount(new BigDecimal("50.01"));

        //when
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        walletService.addAmountToWallet(request);

        //then
        verify(walletRepository, times(1)).save(wallet);
        assertEquals(new BigDecimal("150.02"), wallet.getBalance());
    }

    @Test
    void testRemoveAmountFromWallet() {
        //given
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("100.02"));

        WalletRemoveAmountRequest request = new WalletRemoveAmountRequest();
        request.setWalletId(1L);
        request.setAmount(new BigDecimal("50.02"));

        //when
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        walletService.removeAmountFromWallet(request);

        verify(walletRepository).save(wallet);
        assertEquals(new BigDecimal("50.00"), wallet.getBalance());
    }

    @Test
    void testRemoveAmountFromWallet_and_catch_validation() {
        //given
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(new BigDecimal("100.02"));

        WalletRemoveAmountRequest request = new WalletRemoveAmountRequest();
        request.setWalletId(1L);
        request.setAmount(new BigDecimal("150"));

        //when
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        RestException restException = assertThrows(RestException.class, () -> {
            walletService.removeAmountFromWallet(request);
        });

        //then
        assertEquals(new BigDecimal("100.02"), wallet.getBalance());
        assertEquals("Balance is less then zero. Not allowed.", restException.getMessage());
    }


    @Test
    void testTransferAmount() {
        WalletTransferAmountRequest request = new WalletTransferAmountRequest();
        request.setWalletIdFrom(1L);
        request.setWalletIdTo(2L);
        request.setAmount(new BigDecimal("100"));

        Wallet walletFrom = new Wallet();
        walletFrom.setId(1L);
        walletFrom.setBalance(new BigDecimal("300"));

        Wallet walletTo = new Wallet();
        walletTo.setId(2L);
        walletTo.setBalance(new BigDecimal("100"));

        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletFrom));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(walletTo));

        walletService.transferAmount(request);

        assertEquals(new BigDecimal("200"), walletFrom.getBalance());
        assertEquals(new BigDecimal("200"), walletTo.getBalance());
    }

}
