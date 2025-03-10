package com.iwaitless.services;

import com.iwaitless.persistence.entity.Orders;
import com.iwaitless.persistence.repository.OrdersRepository;
import com.vaadin.flow.component.UI;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<Orders> findAllOrders() {
        return ordersRepository.findAll();
    }

    public Orders findOrderByOrderNo(Long orderNo) {
        if (orderNo == null) {
            throw new IllegalArgumentException("Order number cannot be null.");
        }
        return ordersRepository.findById(orderNo).orElse(null);
    }

    public void saveOrder(Orders order) {
        if (order == null) {
            throw new IllegalArgumentException("Cannot save a null order.");
        }
        ordersRepository.save(order);

        UI.getCurrent().getPage().executeJs("window.notifyUpdate = function() {"
                + "views.com.iwaitless.OrderStatusView.notifyProgressBarUpdate();"
                + "};");
    }
}