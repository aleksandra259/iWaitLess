package com.iwaitless.application.persistence.repository.nomenclatures;


import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {

}
