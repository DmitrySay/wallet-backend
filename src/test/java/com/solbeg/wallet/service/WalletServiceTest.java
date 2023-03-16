package com.solbeg.wallet.service;

import com.solbeg.wallet.dto.WalletAddAmountRequest;
import com.solbeg.wallet.dto.WalletResponse;
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
import static org.mockito.ArgumentMatchers.any;
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
    void addAmountToWallet() {
        //given
        WalletAddAmountRequest request = new WalletAddAmountRequest();
        request.setWalletId(1L);
        request.setAmount(new BigDecimal("5.60"));

        Wallet existingWallet = new Wallet();
        existingWallet.setId(1L);
        existingWallet.setName("wallet");
        existingWallet.setDescription("USD");
        existingWallet.setBalance(new BigDecimal("10.50"));
        Optional<Wallet> mockWallet = Optional.of(existingWallet);

        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setId(1L);
        walletResponse.setName("wallet");
        walletResponse.setDescription("USD");
        walletResponse.setBalance(new BigDecimal("16.10"));

        //when
        when(walletRepository.findById(1L)).thenReturn(mockWallet);
        when(walletMapper.toDto(any(Wallet.class))).thenReturn(walletResponse);

        WalletResponse response = walletService.addAmountToWallet(request);

        //then
        verify(walletRepository, times(1)).save(existingWallet);
        assertEquals(response.getId(), existingWallet.getId());
        assertEquals(response.getName(), existingWallet.getName());
        assertEquals(response.getDescription(), existingWallet.getDescription());
        assertEquals(0, response.getBalance().compareTo(walletResponse.getBalance()));
    }

}
