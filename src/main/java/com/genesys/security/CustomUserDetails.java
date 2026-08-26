package com.genesys.security;

import com.genesys.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user){
        this.user=user;
    }
    public User getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
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

    // Spring Security, her login denemesinde otomatik olarak isEnabled()'a bakıyor.
    // Eğer false dönerse, Spring Security kendi içinde DisabledException fırlatıyor
    // ve login'i reddediyor — biz hiçbir ekstra if (user.isActive()) kontrolü yazmadan.
    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
