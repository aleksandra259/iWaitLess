package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {

}
