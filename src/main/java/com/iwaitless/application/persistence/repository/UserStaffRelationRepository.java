package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.UserStaffRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStaffRelationRepository extends JpaRepository<UserStaffRelation, Long> {

}
