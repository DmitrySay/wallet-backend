package com.solbeg.wallet.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WalletCreateRequest {
    @Size(min = 2)
    private String name;

    private String description;
}
