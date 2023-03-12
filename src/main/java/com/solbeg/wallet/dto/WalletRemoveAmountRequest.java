package com.solbeg.wallet.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRemoveAmountRequest {

    @NotNull
    private Long walletId;
    @DecimalMin(value = "0.01", message = "Amount can not be less than 0.01")
    @Digits(integer = 6, fraction = 2)
    private BigDecimal amount;
}
