package com.upf.violencedetectionbackendlogic.security;

import com.upf.violencedetectionbackendlogic.dao.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final User user; // your domain user

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().getRole().name(); // RoleEnum value
        System.out.println("this is my role name"+roleName);
        return List.of(new SimpleGrantedAuthority(roleName));
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public UUID getId(){
        return user.getId();
    }

    @Override
    public String getUsername() {
        // Use email as username
        return user.getEmail();
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
    public boolean isEnabled() {
        return true;
    }

    // Override equals and hashCode to avoid recursive calls
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

    @Override
    public String toString() {
        return "CustomUserDetails{username=" + getUsername() + "}";
    }

}
