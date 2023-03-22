package com.solbeg.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.wallet.dto.WalletCreateRequest;
import com.solbeg.wallet.dto.WalletResponse;
import com.solbeg.wallet.dto.WalletTransferAmountRequest;
import com.solbeg.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = WalletController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    void testCreateWallet() throws Exception {
        WalletCreateRequest request = new WalletCreateRequest();
        request.setName("Test Wallet");
        request.setDescription("Test Description");

        WalletResponse response = new WalletResponse();
        response.setId(1L);
        response.setName("Test Wallet");
        response.setDescription("Test Description");
        response.setBalance(BigDecimal.ZERO);

        when(walletService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Wallet"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void testTransferAmount() throws Exception {
        WalletTransferAmountRequest request = new WalletTransferAmountRequest();
        request.setAmount(new BigDecimal("100"));
        request.setWalletIdFrom(1L);
        request.setWalletIdTo(2L);

        mockMvc.perform(post("/api/v1/wallets/wallet/amount/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetWallets() throws Exception {
        mockMvc.perform(get("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
