package com.iwaitless.persistence.repository.nomenclatures;


import com.iwaitless.persistence.entity.nomenclatures.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {

}
