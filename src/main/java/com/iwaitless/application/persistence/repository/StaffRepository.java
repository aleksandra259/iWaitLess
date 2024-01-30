package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

}
