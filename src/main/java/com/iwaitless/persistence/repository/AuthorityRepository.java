package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authorities, Long> {

}
