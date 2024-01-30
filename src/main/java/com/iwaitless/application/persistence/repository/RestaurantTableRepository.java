package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

}
