package com.solbeg.wallet.security;

import com.solbeg.wallet.exceptions.RestException;
import com.solbeg.wallet.model.User;
import com.solbeg.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RestException("User was not found."));
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword());
    }
}
