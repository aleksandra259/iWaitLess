package com.iwaitless.services;

import com.iwaitless.persistence.entity.Authorities;
import com.iwaitless.persistence.entity.Users;
import com.iwaitless.persistence.repository.AuthorityRepository;
import com.iwaitless.persistence.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(UsersRepository userRepository,
                       AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public void saveUser(Users user, Authorities authorities) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot save a null user.");
        }
        userRepository.save(user);
        authorityRepository.save(authorities);
    }
}