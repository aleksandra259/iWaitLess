package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Authorities;
import com.iwaitless.application.persistence.entity.Users;
import com.iwaitless.application.persistence.repository.AuthorityRepository;
import com.iwaitless.application.persistence.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UsersRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(UsersRepository userRepository,
                       AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(Users user, Authorities authorities) {
        if (user == null) {
            System.err.println("User is null.");
            return;
        }
        userRepository.save(user);
        authorityRepository.save(authorities);
    }

//    public Users finsUserByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }

}