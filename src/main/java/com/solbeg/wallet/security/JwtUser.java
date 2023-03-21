package com.solbeg.wallet.security;

import com.solbeg.wallet.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class JwtUser extends User implements UserDetails {

    public JwtUser(Long id, String email, String password) {
        super(id, email, password);

    }

    public Long getId() {
        return super.getId();
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    /**
     * Get user roles and permissions amd convert then all to GrantedAuthorities
     *
     * @return authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //todo add roles
        return AuthorityUtils.NO_AUTHORITIES;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
