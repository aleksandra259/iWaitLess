package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    @Query("select c from RestaurantTable c " +
            "where lower(c.description) like lower(concat('%', :searchTerm, '%'))")
    List<RestaurantTable> search(@Param("searchTerm") String searchTerm);
}
