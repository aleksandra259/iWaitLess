package com.iwaitless.application.services;

import com.iwaitless.application.persistence.entity.Orders;
import com.iwaitless.application.persistence.repository.OrdersRepository;
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
        return ordersRepository.findById(orderNo).orElse(null);
    }

    public void saveOrder(Orders order) {
        if (order == null) {
            System.err.println("Order save failed");
            return;
        }
        ordersRepository.save(order);

        UI.getCurrent().getPage().executeJs("window.notifyUpdate = function() {"
                + "com.iwaitless.application.views.OrderStatusView.notifyProgressBarUpdate();"
                + "};");
    }
}