package com.solbeg.wallet.mapper;

import com.solbeg.wallet.dto.WalletResponse;
import com.solbeg.wallet.model.Wallet;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WalletMapper {
    WalletResponse toDto(Wallet wallet);

}
