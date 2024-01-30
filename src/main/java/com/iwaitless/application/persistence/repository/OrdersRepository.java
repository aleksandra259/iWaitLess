package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

}
