package com.ajh.flow.domain;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    @NonNull
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(()->"ROLE_" + user.getRole().name());
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @NonNull
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public void updateUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isAccountNonExpired() {return true;}
    @Override
    public boolean isAccountNonLocked() {return true;}
    @Override
    public boolean isCredentialsNonExpired() {return true;}
    @Override
    public boolean isEnabled() {return true;}



}
