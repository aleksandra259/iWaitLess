package com.iwaitless.application.persistence.repository;


import com.iwaitless.application.persistence.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByOrderNo_OrderNo(Long orderNo);
    int countByOrderNoOrderNo(Long orderNo);
}
