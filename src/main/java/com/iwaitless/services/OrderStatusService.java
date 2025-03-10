package com.iwaitless.services;

import com.iwaitless.persistence.entity.nomenclatures.OrderStatus;
import com.iwaitless.persistence.repository.nomenclatures.OrderStatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusService {

    private final OrderStatusRepository orderStatusRepository;

    public OrderStatusService(OrderStatusRepository orderStatusRepository) {
        this.orderStatusRepository = orderStatusRepository;
    }

    public OrderStatus findStatusById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Status ID cannot be null or empty.");
        }
        return orderStatusRepository.findById(id).orElse(null);
    }

    public OrderStatus findStatusByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Status name cannot be null or empty.");
        }
        return orderStatusRepository.findAll()
                .stream()
                .filter(state -> state.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public List<OrderStatus> findAllStatuses() {
        return orderStatusRepository.findAll();
    }
}
