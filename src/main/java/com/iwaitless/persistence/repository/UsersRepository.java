package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, String> {

}
