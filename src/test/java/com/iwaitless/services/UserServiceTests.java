package com.iwaitless.services;

import com.iwaitless.domain.UserMockData;
import com.iwaitless.persistence.entity.Authorities;
import com.iwaitless.persistence.entity.Users;
import com.iwaitless.persistence.repository.AuthorityRepository;
import com.iwaitless.persistence.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @InjectMocks
    private UserService userService;

    private final Users user = UserMockData.initUser();
    private final Authorities authority = UserMockData.initAuthority();


    @Test
    void testSaveUser() {
        userService.saveUser(user, authority);

        verify(usersRepository, times(1)).save(user);
        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void testSaveUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(null, authority));
        verify(usersRepository, never()).save(any(Users.class));
        verify(authorityRepository, never()).save(any(Authorities.class));
    }
}
