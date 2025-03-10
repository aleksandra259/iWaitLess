package com.iwaitless.persistence.repository;


import com.iwaitless.persistence.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    List<OrderDetails> findByOrder_OrderNo(Long orderNo);
}
