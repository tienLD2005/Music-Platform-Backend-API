package com.ra.base_spring_boot.security.github;

import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomOAuth2User implements OAuth2User, UserDetails {
    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toSet());
    }


    @Override
    public String getName() {
        return user.getLastName();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.getStatus() == UStatus.ACTIVE; }

    public Long getUserId() {
        return user.getId();
    }

    public String getPrimaryRole() {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return "ROLE_USER";
        }
        return user.getRoles().iterator().next().getRoleName().name();
    }

}
