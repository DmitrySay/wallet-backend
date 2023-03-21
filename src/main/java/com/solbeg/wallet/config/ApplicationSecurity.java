package com.solbeg.wallet.config;

import com.solbeg.wallet.security.JwtAuthenticationEntryPoint;
import com.solbeg.wallet.security.JwtAuthenticationFilter;
import com.solbeg.wallet.security.JwtLoginFilter;
import com.solbeg.wallet.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.solbeg.wallet.security.JwtLoginFilter.LOGIN_ENDPOINT;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class ApplicationSecurity {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   JwtTokenProvider jwtTokenProvider) throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager, jwtTokenProvider);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http.cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, LOGIN_ENDPOINT).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  //verify token
                .addFilterAfter(jwtLoginFilter, JwtAuthenticationFilter.class);  // login and create token
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
