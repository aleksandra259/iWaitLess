package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    RestaurantTable findByTableNo (String tableNo);
    @Query("select c from RestaurantTable c " +
            "where lower(c.description) like lower(concat('%', :searchTerm, '%'))")
    List<RestaurantTable> search(@Param("searchTerm") String searchTerm);
}
