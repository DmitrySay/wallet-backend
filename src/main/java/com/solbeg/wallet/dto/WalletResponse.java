package com.solbeg.wallet.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal balance;
}
