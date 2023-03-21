package com.solbeg.wallet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solbeg.wallet.dto.AuthRequestDto;
import com.solbeg.wallet.dto.AuthResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {
    public static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        setFilterProcessesUrl(LOGIN_ENDPOINT);
        setAuthenticationManager(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthRequestDto authRequestDto;
        try {
            authRequestDto = objectMapper.readValue(request.getInputStream(), AuthRequestDto.class);
        } catch (Exception ex) {
            this.logger.error("Failed to read value from authentication request ", ex);
            throw new JwtLoginException(ex.getMessage());
        }

        String username = obtainUsername(authRequestDto);
        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(authRequestDto);
        password = (password != null) ? password : "";

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        String email = authResult.getName();
        //TODO JwtUser jwtUser = (JwtUser) authResult.getPrincipal(); add roles to token - jwtUser.getRoles()
        String token = jwtTokenProvider.createToken(email);
        String authResponseDto = objectMapper.writeValueAsString(new AuthResponseDto(email, token));
        printMessage(response, authResponseDto);
    }

    private String obtainUsername(AuthRequestDto authRequestDto) {
        return authRequestDto.getEmail();
    }

    protected String obtainPassword(AuthRequestDto authRequestDto) {
        return authRequestDto.getPassword();
    }

    private void printMessage(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().println(msg);
    }

}
