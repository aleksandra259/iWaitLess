package com.iwaitless.domain;

import com.iwaitless.persistence.entity.Authorities;
import com.iwaitless.persistence.entity.Users;

public class UserMockData {
    private UserMockData() {}

    public static Users initUser() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setPassword("password123");
        user.setEnabled(true);

        Authorities authority = new Authorities();
        authority.setUsername(user);
        authority.setAuthority("ROLE_USER");

        return user;
    }

    public static Authorities initAuthority() {
        Authorities authority = new Authorities();
        authority.setUsername(initUser());
        authority.setAuthority("ROLE_USER");

        return authority;
    }
}
