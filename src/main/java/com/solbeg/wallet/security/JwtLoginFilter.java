package com.solbeg.wallet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.wallet.dto.AuthRequestDto;
import com.solbeg.wallet.dto.AuthResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtLoginFilter(String loginUrl, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        setFilterProcessesUrl(loginUrl);
        setAuthenticationManager(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthRequestDto authRequestDto;
        try {
            authRequestDto = new ObjectMapper().readValue(request.getInputStream(), AuthRequestDto.class);
        } catch (Exception ex) {
            log.error("Failed to read value from authentication request ", ex);
            throw new RuntimeException(ex);
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(), authRequestDto.getPassword());
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String email = authResult.getName();
        //JwtUser jwtUser = (JwtUser) authResult.getPrincipal();
        String token = jwtTokenProvider.createToken(email);
        ObjectMapper objectMapper = new ObjectMapper();
        String authResponseDto = objectMapper.writeValueAsString(new AuthResponseDto(email, token));
        printMessage(response, authResponseDto);
    }

    private void printMessage(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().println(msg);
    }

}
