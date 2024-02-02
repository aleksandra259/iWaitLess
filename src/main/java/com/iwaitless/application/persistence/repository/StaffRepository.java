package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query("select c from Staff c " +
            "where lower(c.firstName) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))")
    List<Staff> search(@Param("searchTerm") String searchTerm);
}
