package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.UserStaffRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStaffRelationRepository extends JpaRepository<UserStaffRelation, Long> {

}
