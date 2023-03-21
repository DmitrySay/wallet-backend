package com.solbeg.wallet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String BEARER = "Bearer ";

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(@Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private Key key;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", "");
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) throws AuthenticationException {
        try {
            String username = getUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
        } catch (Exception exception) {
            throw new BadCredentialsException("JWT token invalid.");
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            return null;
        }
        return bearerToken.substring(BEARER.length());
    }

    /**
     * Check if token valid and not expired.
     *
     * @param token
     * @return true/false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("JWT token is expired or invalid.");
        }
    }

}
