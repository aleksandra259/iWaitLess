package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Ingredients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

}
