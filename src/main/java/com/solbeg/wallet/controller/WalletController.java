package com.solbeg.wallet.controller;


import com.solbeg.wallet.dto.WalletAddAmountRequest;
import com.solbeg.wallet.dto.WalletCreateRequest;
import com.solbeg.wallet.dto.WalletRemoveAmountRequest;
import com.solbeg.wallet.dto.WalletResponse;
import com.solbeg.wallet.dto.WalletTransferAmountRequest;
import com.solbeg.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Page<WalletResponse> getWallets(@PageableDefault Pageable pageable) {
        return walletService.getWallets(pageable);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public WalletResponse createWallet(@Valid @RequestBody WalletCreateRequest request) {
        return walletService.create(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/wallet/amount/add")
    public WalletResponse addAmount(@Valid @RequestBody WalletAddAmountRequest request) {
        return walletService.addAmountToWallet(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/wallet/amount/remove")
    public WalletResponse removeAmount(@Valid @RequestBody WalletRemoveAmountRequest request) {
        return walletService.removeAmountFromWallet(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/wallet/amount/transfer")
    public void transferAmount(@Valid @RequestBody WalletTransferAmountRequest request) {
        walletService.transferAmount(request);
    }

}
