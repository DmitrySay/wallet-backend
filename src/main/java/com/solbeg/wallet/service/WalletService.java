package com.solbeg.wallet.service;


import com.solbeg.wallet.dto.WalletAddAmountRequest;
import com.solbeg.wallet.dto.WalletCreateRequest;
import com.solbeg.wallet.dto.WalletRemoveAmountRequest;
import com.solbeg.wallet.dto.WalletResponse;
import com.solbeg.wallet.exceptions.RestException;
import com.solbeg.wallet.mapper.WalletMapper;
import com.solbeg.wallet.model.Wallet;
import com.solbeg.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Transactional(readOnly = true)
    public Wallet getById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RestException("Wallet is not found."));
    }

    @Transactional
    public WalletResponse create(WalletCreateRequest request) {
        Wallet wallet = new Wallet();
        wallet.setName(request.getName());
        wallet.setDescription(request.getDescription());
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Transactional(readOnly = true)
    public Page<WalletResponse> getWallets(Pageable pageable) {
        Page<Wallet> wallets = walletRepository.findAll(pageable);
        return wallets.map(walletMapper::toDto);
    }

    @Transactional
    public WalletResponse addAmountToWallet(WalletAddAmountRequest request) {
        Wallet wallet = getById(request.getWalletId());
        BigDecimal balance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(balance);
        walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Transactional
    public WalletResponse removeAmountFromWallet(WalletRemoveAmountRequest request) {
        Wallet wallet = getById(request.getWalletId());
        BigDecimal balance = wallet.getBalance().subtract(request.getAmount());
        if (BigDecimal.ZERO.compareTo(balance) > 0) {
            throw new RestException("Balance is less then zero. Not allowed.");
        }
        wallet.setBalance(balance);
        walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }
}
