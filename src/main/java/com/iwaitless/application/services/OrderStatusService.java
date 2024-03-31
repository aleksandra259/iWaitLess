package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.application.persistence.repository.nomenclatures.OrderStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;


    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public OrderStatus findStatusById(String id) {
        return orderStatusRepository.findById(id).orElse(null);
    }
}