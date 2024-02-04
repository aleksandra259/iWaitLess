package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authorities, Long> {

}
