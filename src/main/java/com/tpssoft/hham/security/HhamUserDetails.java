package com.tpssoft.hham.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HhamUserDetails extends org.springframework.security.core.userdetails.User {
    @Getter
    private final int id;
    @Getter
    private final String token;
    @Getter
    private final String displayName;
    @Getter
    private final String email;

    public HhamUserDetails(int id, String username, String displayName, String email,
                           String hashedPassword, String token,
                           Collection<? extends GrantedAuthority> authorities) {
        super(username, hashedPassword, authorities);
        this.id = id;
        this.token = token;
        this.displayName = displayName;
        this.email = email;
    }
}
